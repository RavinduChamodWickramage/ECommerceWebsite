package com.practice.ecommerce.service.customer.cart;

import com.practice.ecommerce.dto.AddProductInCartDto;
import com.practice.ecommerce.dto.CartItemsDto;
import com.practice.ecommerce.dto.OrderDto;
import com.practice.ecommerce.dto.PlaceOrderDto;
import com.practice.ecommerce.entity.*;
import com.practice.ecommerce.enums.OrderStatus;
import com.practice.ecommerce.exception.ValidationException;
import com.practice.ecommerce.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Override
    public ResponseEntity<?> addProductInCart(AddProductInCartDto addProductInCartDto) {

        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(addProductInCartDto.getUserId(), OrderStatus.PENDING);


        if (activeOrder == null) {
            User user = userRepository.findById(addProductInCartDto.getUserId()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }


            activeOrder = new Order();
            activeOrder.setUser(user);
            activeOrder.setOrderStatus(OrderStatus.PENDING);
            activeOrder.setTotalAmount(BigDecimal.ZERO);
            activeOrder.setAmount(BigDecimal.ZERO);
            activeOrder.setCartItems(new ArrayList<>());


            orderRepository.save(activeOrder);
        }


        Optional<CartItems> optionalCartItems =
                cartItemsRepository.findByProductIdAndOrderIdAndUserId(addProductInCartDto.getProductId(),
                        activeOrder.getId(), addProductInCartDto.getUserId());

        if (optionalCartItems.isPresent()) {

            CartItems cartItem = optionalCartItems.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartItemsRepository.save(cartItem);


            activeOrder.setTotalAmount(activeOrder.getTotalAmount().add(cartItem.getPrice()));
            activeOrder.setAmount(activeOrder.getAmount().add(cartItem.getPrice()));
            orderRepository.save(activeOrder);

            return ResponseEntity.status(HttpStatus.OK).body(cartItem.getCartDto());
        } else {

            Optional<Product> optionalProduct = productRepository.findById(addProductInCartDto.getProductId());
            Optional<User> optionalUser = userRepository.findById(addProductInCartDto.getUserId());

            if (optionalProduct.isPresent() && optionalUser.isPresent()) {

                CartItems cart = new CartItems();
                cart.setProduct(optionalProduct.get());
                cart.setPrice((optionalProduct.get().getPrice()));
                cart.setQuantity(1L);
                cart.setUser(optionalUser.get());
                cart.setOrder(activeOrder);

                cartItemsRepository.save(cart);

                activeOrder.getCartItems().add(cart);

                activeOrder.setTotalAmount(activeOrder.getTotalAmount().add(cart.getPrice()));
                activeOrder.setAmount(activeOrder.getAmount().add(cart.getPrice()));
                orderRepository.save(activeOrder);

                return ResponseEntity.status(HttpStatus.CREATED).body(cart.getCartDto());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or Product not found.");
            }
        }
    }




    @Override
    public OrderDto getCartByUserId(Long userId) {
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.PENDING);

        if (activeOrder == null) {
            activeOrder = new Order();
            User user = userRepository.findById(userId).orElseThrow(() -> new ValidationException("User not found."));
            activeOrder.setUser(user);
            activeOrder.setOrderStatus(OrderStatus.PENDING);
            activeOrder.setTotalAmount(BigDecimal.ZERO);
            activeOrder.setAmount(BigDecimal.ZERO);
            activeOrder.setCartItems(new ArrayList<>());

            orderRepository.save(activeOrder);
        }

        List<CartItemsDto> cartItemsDtoList =
                activeOrder.getCartItems().stream().map(CartItems::getCartDto).collect(Collectors.toList());

        OrderDto orderDto = new OrderDto();
        orderDto.setAmount(activeOrder.getAmount());
        orderDto.setId(activeOrder.getId());
        orderDto.setOrderStatus(activeOrder.getOrderStatus());
        orderDto.setDiscount(activeOrder.getDiscount());
        orderDto.setTotalAmount(activeOrder.getTotalAmount());
        orderDto.setCartItems(cartItemsDtoList);

        if (activeOrder.getCoupon() != null) {
            orderDto.setCouponName(activeOrder.getCoupon().getName());
        }

        return orderDto;
    }

    @Override
    public OrderDto applyCoupon(Long userId, String code) {

        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(userId,OrderStatus.PENDING);
        if (activeOrder == null) {
            throw new ValidationException("No active order found for user.");
        }

        Coupon coupon = couponRepository.findByCode(code).orElseThrow(() -> new ValidationException("Coupon not found."));

        if (couponIsExpired(coupon)) {
            throw new ValidationException("Coupon is expired.");
        }

        BigDecimal discountPercentage = coupon.getDiscount().divide(BigDecimal.valueOf(100));
        BigDecimal discountAmount = activeOrder.getTotalAmount().multiply(discountPercentage);
        BigDecimal netAmount = activeOrder.getTotalAmount().subtract(discountAmount);


        activeOrder.setDiscount(discountAmount);
        activeOrder.setAmount(netAmount);
        activeOrder.setCoupon(coupon);

        orderRepository.save(activeOrder);
        return activeOrder.getOrderDto();
    }

    @Override
    @Transactional
    public OrderDto increaseProductQuantity(AddProductInCartDto addProductInCartDto) {
        if (addProductInCartDto.getProductId() == null || addProductInCartDto.getUserId() == null) {
            throw new ValidationException("Invalid productId or userId.");
        }

        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(addProductInCartDto.getUserId(), OrderStatus.PENDING);
        if (activeOrder == null) {
            throw new ValidationException("No active order found for user.");
        }

        Optional<Product> optionalProduct = productRepository.findById(addProductInCartDto.getProductId());
        if (optionalProduct.isEmpty()) {
            throw new ValidationException("Product not found.");
        }

        Product product = optionalProduct.get();

        Optional<CartItems> optionalCartItems = cartItemsRepository.findByProductIdAndOrderIdAndUserId(
                addProductInCartDto.getProductId(), activeOrder.getId(), addProductInCartDto.getUserId());
        if (optionalCartItems.isEmpty()) {
            throw new ValidationException("Cart item not found for this product. Add it to the cart first.");
        }

        CartItems cartItem = optionalCartItems.get();
        cartItem.setQuantity(cartItem.getQuantity() + 1);

        activeOrder.setAmount(activeOrder.getAmount().add(product.getPrice()));
        activeOrder.setTotalAmount(activeOrder.getTotalAmount().add(product.getPrice()));

        if (activeOrder.getCoupon() != null) {
            BigDecimal discountAmount = activeOrder.getTotalAmount()
                    .multiply(activeOrder.getCoupon().getDiscount())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            BigDecimal netAmount = activeOrder.getTotalAmount().subtract(discountAmount);

            activeOrder.setDiscount(discountAmount);
            activeOrder.setAmount(netAmount);
        }

        cartItemsRepository.save(cartItem);
        orderRepository.save(activeOrder);

        return activeOrder.getOrderDto();
    }


    @Override
    @Transactional
    public OrderDto decreaseProductQuantity(AddProductInCartDto addProductInCartDto) {
        if (addProductInCartDto.getProductId() == null || addProductInCartDto.getUserId() == null) {
            throw new ValidationException("Invalid productId or userId.");
        }

        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(addProductInCartDto.getUserId(), OrderStatus.PENDING);
        if (activeOrder == null) {
            throw new ValidationException("No active order found for user.");
        }

        Optional<Product> optionalProduct = productRepository.findById(addProductInCartDto.getProductId());
        if (optionalProduct.isEmpty()) {
            throw new ValidationException("Product not found.");
        }

        Product product = optionalProduct.get();

        Optional<CartItems> optionalCartItems = cartItemsRepository.findByProductIdAndOrderIdAndUserId(
                addProductInCartDto.getProductId(), activeOrder.getId(), addProductInCartDto.getUserId());

        if (optionalCartItems.isEmpty()) {
            throw new ValidationException("Cart item not found for this product. Add it to the cart first.");
        }

        CartItems cartItem = optionalCartItems.get();
        cartItem.setQuantity(cartItem.getQuantity() - 1);

        if (cartItem.getQuantity() == 0) {
            cartItemsRepository.delete(cartItem);
        }

        activeOrder.setAmount(activeOrder.getAmount().subtract(product.getPrice()));
        activeOrder.setTotalAmount(activeOrder.getTotalAmount().subtract(product.getPrice()));

        if (activeOrder.getCoupon() != null) {
            BigDecimal discountAmount = activeOrder.getTotalAmount()
                    .multiply(activeOrder.getCoupon().getDiscount())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            BigDecimal netAmount = activeOrder.getTotalAmount().subtract(discountAmount);

            activeOrder.setDiscount(discountAmount);
            activeOrder.setAmount(netAmount);
        }

        orderRepository.save(activeOrder);
        return activeOrder.getOrderDto();
    }

    @Override
    @Transactional
    public OrderDto removeProductFromCart(Long productId, Long userId) {
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.PENDING);
        if (activeOrder == null) {
            throw new ValidationException("No active order found for user.");
        }

        Optional<CartItems> optionalCartItems = cartItemsRepository.findByProductIdAndOrderIdAndUserId(productId, activeOrder.getId(), userId);
        if (optionalCartItems.isEmpty()) {
            throw new ValidationException("Product not found in the cart.");
        }

        CartItems cartItem = optionalCartItems.get();

        activeOrder.setAmount(activeOrder.getAmount().subtract(cartItem.getPrice()));
        activeOrder.setTotalAmount(activeOrder.getTotalAmount().subtract(cartItem.getPrice()));

        if (activeOrder.getCoupon() != null) {
            BigDecimal discountAmount = activeOrder.getTotalAmount()
                    .multiply(activeOrder.getCoupon().getDiscount())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            BigDecimal netAmount = activeOrder.getTotalAmount().subtract(discountAmount);

            activeOrder.setDiscount(discountAmount);
            activeOrder.setAmount(netAmount);
        }

        cartItemsRepository.delete(cartItem);

        if (activeOrder.getCartItems().isEmpty()) {
            orderRepository.delete(activeOrder);
        } else {
            orderRepository.save(activeOrder);
        }

        return activeOrder.getOrderDto();
    }

    @Override
    @Transactional
    public OrderDto clearCart(Long userId) {
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.PENDING);
        if (activeOrder == null) {
            throw new ValidationException("No active order found for user.");
        }

        List<CartItems> cartItems = cartItemsRepository.findByOrderId(activeOrder.getId());

        cartItemsRepository.deleteAll(cartItems);

        orderRepository.delete(activeOrder);

        return activeOrder.getOrderDto();
    }

    @Override
    @Transactional
    public OrderDto placeOrder(PlaceOrderDto placeOrderDto) {
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(placeOrderDto.getUserId(), OrderStatus.PENDING);

        if (activeOrder == null) {
            Optional<User> optionalUser = userRepository.findById(placeOrderDto.getUserId());

            if (optionalUser.isPresent()) {
                activeOrder = new Order();
                activeOrder.setUser(optionalUser.get());
                activeOrder.setOrderStatus(OrderStatus.PENDING);
                activeOrder.setTrackingId(UUID.randomUUID());
                activeOrder.setOrderDescription(placeOrderDto.getOrderDescription());
            } else {
                return null;
            }
        }

        activeOrder.setOrderDescription(placeOrderDto.getOrderDescription());
        activeOrder.setAddress(placeOrderDto.getAddress());
        activeOrder.setDate(new Date());
        activeOrder.setOrderStatus(OrderStatus.PLACED);
        activeOrder.setTrackingId(UUID.randomUUID());

        orderRepository.save(activeOrder);

        return activeOrder.getOrderDto();
    }


    @Override
    public List<OrderDto> getMyPlacedOrders(Long userId) {
        return orderRepository.findByUserIdAndOrderStatusIn(userId, List.of(OrderStatus.PLACED, OrderStatus.SHIPPED, OrderStatus.DELIVERED))
                .stream()
                .map(Order::getOrderDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto searchOrderByTrackingId(UUID trackingId) {
        Optional<Order> optionalOrder = orderRepository.findByTrackingId(trackingId);

        if (optionalOrder.isPresent()) {
            return optionalOrder.get().getOrderDto();
        }
        return null;
    }


    private boolean couponIsExpired(Coupon coupon) {
        Date currentDate = new Date();
        Date expirationDate = coupon.getExpirationDate();

        return expirationDate != null && currentDate.after(expirationDate);
    }
}