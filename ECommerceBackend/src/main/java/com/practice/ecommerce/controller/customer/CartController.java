package com.practice.ecommerce.controller.customer;

import com.practice.ecommerce.dto.AddProductInCartDto;
import com.practice.ecommerce.dto.OrderDto;
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
}
