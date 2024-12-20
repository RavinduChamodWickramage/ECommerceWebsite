package com.practice.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WishlistDto {

    private Long userId;

    private Long productId;

    private Long id;

    private String productName;

    private String productDescription;

    private byte[] returnedImg;

    private BigDecimal price;
}
