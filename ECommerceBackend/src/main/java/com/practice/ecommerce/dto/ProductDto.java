package com.practice.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductDto {

    private Long id;

    private String name;

    private double price;

    private String description;

//    private byte[] byteImg;
    private String byteImg;

    private Long categoryId;

    private String categoryName;

    @JsonIgnore
    private MultipartFile img;
}
