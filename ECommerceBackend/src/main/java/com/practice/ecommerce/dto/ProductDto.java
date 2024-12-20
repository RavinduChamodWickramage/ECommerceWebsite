package com.practice.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
public class ProductDto {

    private Long id;

    private String name;

    private BigDecimal price;

    private String description;

    private String byteImg;

    private Long categoryId;

    private String categoryName;

    @JsonIgnore
    private MultipartFile img;

    private Long quantity;
}
