package com.practice.ecommerce.dto;

import lombok.Data;

@Data
public class FAQDto {

    private Long id;

    private String question;

    private String answer;

    private Long productId;
}
