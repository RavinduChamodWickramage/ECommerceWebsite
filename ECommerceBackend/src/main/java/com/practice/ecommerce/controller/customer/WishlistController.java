package com.practice.ecommerce.controller.customer;

import com.practice.ecommerce.dto.WishlistDto;
import com.practice.ecommerce.service.customer.customer.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/wishlist")
    public ResponseEntity<?> addProductToWishlist(@RequestBody WishlistDto wishlistDto) {
        WishlistDto postedWishlistDto = wishlistService.addProductToWishlist(wishlistDto);

        if (postedWishlistDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(postedWishlistDto);
    }

    @GetMapping("/wishlist/{userId}")
    public ResponseEntity<List<WishlistDto>> getWishlistByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(wishlistService.getWishlistByUserId(userId));
    }

    @GetMapping("/wishlist/check/{userId}/{productId}")
    public ResponseEntity<Boolean> checkProductInWishlist(@PathVariable Long userId, @PathVariable Long productId) {
        boolean isInWishlist = wishlistService.isProductInWishlist(userId, productId);
        return ResponseEntity.ok(isInWishlist);
    }

}
