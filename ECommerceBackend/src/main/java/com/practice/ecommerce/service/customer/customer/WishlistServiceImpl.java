package com.practice.ecommerce.service.customer.customer;

import com.practice.ecommerce.dto.WishlistDto;
import com.practice.ecommerce.entity.Product;
import com.practice.ecommerce.entity.User;
import com.practice.ecommerce.entity.Wishlist;
import com.practice.ecommerce.repository.ProductRepository;
import com.practice.ecommerce.repository.UserRepository;
import com.practice.ecommerce.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final WishlistRepository wishlistRepository;


    @Override
    public WishlistDto addProductToWishlist(WishlistDto wishlistDto) {
        Optional<Product> optionalProduct = productRepository.findById(wishlistDto.getProductId());
        Optional<User> optionalUser = userRepository.findById(wishlistDto.getUserId());

        if (optionalProduct.isPresent() && optionalUser.isPresent()) {
            Wishlist wishlist = new Wishlist();
            wishlist.setProduct(optionalProduct.get());
            wishlist.setUser(optionalUser.get());

            return wishlistRepository.save(wishlist).getWishlistDto();
        }
        return null;
    }

    @Override
    public List<WishlistDto> getWishlistByUserId(Long userId) {
        return wishlistRepository.findAllByUserId(userId).stream().map(Wishlist::getWishlistDto).collect(Collectors.toList());
    }

    @Override
    public boolean isProductInWishlist(Long userId, Long productId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }
}
