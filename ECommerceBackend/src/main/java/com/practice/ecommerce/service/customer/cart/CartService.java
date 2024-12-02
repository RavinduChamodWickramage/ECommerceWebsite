package com.practice.ecommerce.service.customer.cart;

import com.practice.ecommerce.dto.AddProductInCartDto;
import com.practice.ecommerce.dto.OrderDto;
import com.practice.ecommerce.entity.Coupon;
import org.springframework.http.ResponseEntity;

public interface CartService {

    ResponseEntity<?> addProductInCart(AddProductInCartDto addProductInCartDto);

    OrderDto getCartByUserId(Long userId);

    OrderDto applyCoupon(Long userId, String code);
}
