package com.practice.ecommerce.service.customer.cart;

import com.practice.ecommerce.dto.AddProductInCartDto;
import com.practice.ecommerce.dto.CartItemsDto;
import com.practice.ecommerce.dto.OrderDto;
import com.practice.ecommerce.entity.*;
import com.practice.ecommerce.enums.OrderStatus;
import com.practice.ecommerce.exception.ValidationException;
import com.practice.ecommerce.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
            activeOrder.setTotalAmount(0L);
            activeOrder.setAmount(0L);

            if (activeOrder.getCartItems() == null) {
                activeOrder.setCartItems(new ArrayList<>());
            }

            orderRepository.save(activeOrder);
        }

        Optional<CartItems> optionalCartItems =
                cartItemsRepository.findByProductIdAndOrderIdAndUserId(addProductInCartDto.getProductId(),
                        activeOrder.getId(), addProductInCartDto.getUserId());

        if (optionalCartItems.isPresent()) {
            CartItems cartItem = optionalCartItems.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartItemsRepository.save(cartItem);

            activeOrder.setTotalAmount(activeOrder.getTotalAmount() + cartItem.getPrice());
            activeOrder.setAmount(activeOrder.getAmount() + cartItem.getPrice());
            orderRepository.save(activeOrder);

            return ResponseEntity.status(HttpStatus.OK).body(cartItem.getCartDto());
        } else {
            Optional<Product> optionalProduct = productRepository.findById(addProductInCartDto.getProductId());
            Optional<User> optionalUser = userRepository.findById(addProductInCartDto.getUserId());

            if (optionalProduct.isPresent() && optionalUser.isPresent()) {
                CartItems cart = new CartItems();
                cart.setProduct(optionalProduct.get());
                cart.setPrice((long) optionalProduct.get().getPrice());
                cart.setQuantity(1L);
                cart.setUser(optionalUser.get());
                cart.setOrder(activeOrder);

                cartItemsRepository.save(cart);

                if (activeOrder.getCartItems() == null) {
                    activeOrder.setCartItems(new ArrayList<>());
                }

                activeOrder.getCartItems().add(cart);

                activeOrder.setTotalAmount(activeOrder.getTotalAmount() + cart.getPrice());
                activeOrder.setAmount(activeOrder.getAmount() + cart.getPrice());
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

        double discountAmount = ((coupon.getDiscount() / 100.0) * activeOrder.getTotalAmount());
        double netAmount = activeOrder.getTotalAmount() - discountAmount;

        activeOrder.setDiscount((long) discountAmount);
        activeOrder.setAmount((long) netAmount);
        activeOrder.setCoupon(coupon);

        orderRepository.save(activeOrder);
        return activeOrder.getOrderDto();
    }

    @Override
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

        activeOrder.setAmount((long) (activeOrder.getAmount() + product.getPrice()));
        activeOrder.setTotalAmount((long) (activeOrder.getTotalAmount() + product.getPrice()));

        if (activeOrder.getCoupon() != null) {
            double discountAmount = ((activeOrder.getCoupon().getDiscount() / 100.0) * activeOrder.getTotalAmount());
            double netAmount = activeOrder.getTotalAmount() - discountAmount;

            activeOrder.setDiscount((long) discountAmount);
            activeOrder.setAmount((long) netAmount);
        }

        cartItemsRepository.save(cartItem);
        orderRepository.save(activeOrder);

        return activeOrder.getOrderDto();
    }

    @Override
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

        activeOrder.setAmount((long) (activeOrder.getAmount() - product.getPrice()));
        activeOrder.setTotalAmount((long) (activeOrder.getTotalAmount() - product.getPrice()));

        if (activeOrder.getCoupon() != null) {
            double discountAmount = ((activeOrder.getCoupon().getDiscount() / 100.0) * activeOrder.getTotalAmount());
            double netAmount = activeOrder.getTotalAmount() - discountAmount;

            activeOrder.setDiscount((long) discountAmount);
            activeOrder.setAmount((long) netAmount);
        }

        cartItemsRepository.save(cartItem);
        orderRepository.save(activeOrder);
        return activeOrder.getOrderDto();
    }

    @Override
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

        activeOrder.setAmount(activeOrder.getAmount() - cartItem.getPrice());
        activeOrder.setTotalAmount(activeOrder.getTotalAmount() - cartItem.getPrice());

        if (activeOrder.getCoupon() != null) {
            double discountAmount = ((activeOrder.getCoupon().getDiscount() / 100.0) * activeOrder.getTotalAmount());
            double netAmount = activeOrder.getTotalAmount() - discountAmount;

            activeOrder.setDiscount((long) discountAmount);
            activeOrder.setAmount((long) netAmount);
        }

        cartItemsRepository.delete(cartItem);
        orderRepository.save(activeOrder);

        return activeOrder.getOrderDto();
    }

    @Override
    public OrderDto clearCart(Long userId) {
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.PENDING);
        if (activeOrder == null) {
            throw new ValidationException("No active order found for user.");
        }

        List<CartItems> cartItems = cartItemsRepository.findByOrderId(activeOrder.getId());

        for (CartItems cartItem : cartItems) {
            Optional<CartItems> itemOptional = cartItemsRepository.findByProductIdAndOrderIdAndUserId(
                    cartItem.getProduct().getId(), activeOrder.getId(), userId);

            itemOptional.ifPresent(cartItemsRepository::delete);
        }

        orderRepository.delete(activeOrder);

        return activeOrder.getOrderDto();
    }



    private boolean couponIsExpired(Coupon coupon) {
        Date currentDate = new Date();
        Date expirationDate = coupon.getExpirationDate();

        return expirationDate != null && currentDate.after(expirationDate);
    }
}
