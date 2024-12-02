package com.practice.ecommerce.service.customer.cart;

import com.practice.ecommerce.dto.AddProductInCartDto;
import com.practice.ecommerce.dto.OrderDto;
import org.springframework.http.ResponseEntity;

public interface CartService {

    ResponseEntity<?> addProductInCart(AddProductInCartDto addProductInCartDto);

     OrderDto getCartByUserId(Long userId);
}
