package com.practice.ecommerce.service.customer.product;

import com.practice.ecommerce.dto.ProductDetailDto;
import com.practice.ecommerce.dto.ProductDto;

import java.util.List;

public interface CustomerProductService {

    List<ProductDto> getAllProducts();

    List<ProductDto> searchProductByName(String name);

    ProductDetailDto getProductDetailById(Long productId);
}
