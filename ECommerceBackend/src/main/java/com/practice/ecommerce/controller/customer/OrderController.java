package com.practice.ecommerce.controller.customer;

import com.practice.ecommerce.dto.OrderDto;
import com.practice.ecommerce.dto.PlaceOrderDto;
import com.practice.ecommerce.service.customer.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class OrderController {

    private final CartService cartService;

    @PostMapping("/placeOrder")
    public ResponseEntity<OrderDto> placeOrder(@RequestBody PlaceOrderDto placeOrderDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.placeOrder(placeOrderDto));
    }

    @GetMapping("/myOrders/{userId}")
    public ResponseEntity<List<OrderDto>> getMyPlacedOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getMyPlacedOrders(userId));
    }
}
