package com.practice.ecommerce.controller.customer;

import com.practice.ecommerce.dto.AddProductInCartDto;
import com.practice.ecommerce.dto.OrderDto;
import com.practice.ecommerce.exception.ForbiddenException;
import com.practice.ecommerce.exception.ValidationException;
import com.practice.ecommerce.service.customer.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/cart")
    public ResponseEntity<?> addProductToCart(@RequestBody AddProductInCartDto addProductInCartDto) {
        return cartService.addProductInCart(addProductInCartDto);
    }

    @GetMapping("/cart/{userId}")
    public ResponseEntity<?> getCartByUserId(@PathVariable Long userId) {
        OrderDto orderDto = cartService.getCartByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(orderDto);
    }

    @GetMapping("/coupon/{userId}/{code}")
    public ResponseEntity<?> applyCoupon(@PathVariable Long userId, @PathVariable String code) {
        try {
            OrderDto orderDto = cartService.applyCoupon(userId, code);
            return ResponseEntity.ok(orderDto);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }

    }

    @PostMapping("/addition")
    public ResponseEntity<OrderDto> increaseProductQuantity(@RequestBody AddProductInCartDto addProductInCartDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.increaseProductQuantity(addProductInCartDto));
    }

    @PostMapping("/decrease")
    public ResponseEntity<OrderDto> decreaseProductQuantity(@RequestBody AddProductInCartDto addProductInCartDto) {
        return ResponseEntity.status(HttpStatus.OK).body(cartService.decreaseProductQuantity(addProductInCartDto));
    }

    @DeleteMapping("/remove/{productId}/{userId}")
    public ResponseEntity<OrderDto> removeProductFromCart(@PathVariable Long productId, @PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(cartService.removeProductFromCart(productId, userId));
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<OrderDto> clearCart(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(cartService.clearCart(userId));
    }

}
