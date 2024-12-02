package com.practice.ecommerce.service.customer.cart;

import com.practice.ecommerce.dto.AddProductInCartDto;
import com.practice.ecommerce.dto.CartItemsDto;
import com.practice.ecommerce.dto.OrderDto;
import com.practice.ecommerce.entity.CartItems;
import com.practice.ecommerce.entity.Order;
import com.practice.ecommerce.entity.Product;
import com.practice.ecommerce.entity.User;
import com.practice.ecommerce.enums.OrderStatus;
import com.practice.ecommerce.repository.CartItemsRepository;
import com.practice.ecommerce.repository.OrderRepository;
import com.practice.ecommerce.repository.ProductRepository;
import com.practice.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Override
    public ResponseEntity<?> addProductInCart(AddProductInCartDto addProductInCartDto) {
        // Fetch the active order (pending status)
        Order activeOrder = orderRepository.findByUserIdAndOrderStatus(addProductInCartDto.getUserId(), OrderStatus.PENDING);

        // If no active order, create a new one
        if (activeOrder == null) {
            User user = userRepository.findById(addProductInCartDto.getUserId()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            // Create a new order for the user
            activeOrder = new Order();
            activeOrder.setUser(user);
            activeOrder.setOrderStatus(OrderStatus.PENDING);
            activeOrder.setTotalAmount(0L);  // Set initial total amount to 0
            activeOrder.setAmount(0L);  // Set initial amount to 0

            // Ensure cartItems list is initialized
            if (activeOrder.getCartItems() == null) {
                activeOrder.setCartItems(new ArrayList<>());
            }

            orderRepository.save(activeOrder);

            System.out.println("Created a new active order for userId: " + addProductInCartDto.getUserId());
        }

        // Continue with the logic for adding product to the cart
        Optional<CartItems> optionalCartItems =
                cartItemsRepository.findByProductIdAndOrderIdAndUserId(addProductInCartDto.getProductId(),
                        activeOrder.getId(), addProductInCartDto.getUserId());

        if (optionalCartItems.isPresent()) {
            // Product is already in the cart, so update the quantity
            CartItems cartItem = optionalCartItems.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartItemsRepository.save(cartItem);

            // Update the order's total amount
            activeOrder.setTotalAmount(activeOrder.getTotalAmount() + cartItem.getPrice());
            activeOrder.setAmount(activeOrder.getAmount() + cartItem.getPrice());
            orderRepository.save(activeOrder);

            return ResponseEntity.status(HttpStatus.OK).body(cartItem.getCartDto());
        } else {
            // Product is not in the cart, so add it
            Optional<Product> optionalProduct = productRepository.findById(addProductInCartDto.getProductId());
            Optional<User> optionalUser = userRepository.findById(addProductInCartDto.getUserId());

            if (optionalProduct.isPresent() && optionalUser.isPresent()) {
                CartItems cart = new CartItems();
                cart.setProduct(optionalProduct.get());
                cart.setPrice((long) optionalProduct.get().getPrice());
                cart.setQuantity(1L);  // Add the product with quantity 1
                cart.setUser(optionalUser.get());
                cart.setOrder(activeOrder);

                // Save the new cart item
                cartItemsRepository.save(cart);

                // Ensure cartItems list is initialized before adding the item
                if (activeOrder.getCartItems() == null) {
                    activeOrder.setCartItems(new ArrayList<>());
                }

                // Add the new cart item to the order
                activeOrder.getCartItems().add(cart);

                // Update the order's total amount
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

        return orderDto;
    }
}
