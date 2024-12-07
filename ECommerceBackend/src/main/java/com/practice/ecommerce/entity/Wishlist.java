package com.practice.ecommerce.entity;

import com.practice.ecommerce.dto.WishlistDto;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Base64;

@Data
@Entity
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    public WishlistDto getWishlistDto() {
        WishlistDto wishlistDto = new WishlistDto();
        wishlistDto.setId(id);
        wishlistDto.setProductId(product.getId());
        wishlistDto.setProductName(product.getName());
        wishlistDto.setProductDescription(product.getDescription());
        wishlistDto.setReturnedImg(product.getImg());
        wishlistDto.setPrice((long) product.getPrice());
        wishlistDto.setUserId(user.getId());
        return wishlistDto;
    }
}
