package com.practice.ecommerce.service.customer;

import com.practice.ecommerce.dto.ProductDto;

import java.util.List;

public interface CustomerProductService {

    List<ProductDto> getAllProducts();

    List<ProductDto> searchProductByName(String name);
}
