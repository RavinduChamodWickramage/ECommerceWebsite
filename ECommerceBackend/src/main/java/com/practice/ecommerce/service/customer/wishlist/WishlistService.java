package com.practice.ecommerce.service.customer.wishlist;

import com.practice.ecommerce.dto.WishlistDto;

import java.util.List;

public interface WishlistService {

    WishlistDto addProductToWishlist(WishlistDto wishlistDto);

    List<WishlistDto> getWishlistByUserId(Long userId);

    boolean isProductInWishlist(Long userId, Long productId);
}
