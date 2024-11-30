package com.practice.ecommerce.service.admin.product;

import com.practice.ecommerce.dto.ProductDto;

import java.io.IOException;
import java.util.List;

public interface ProductService{

    ProductDto addProduct(ProductDto productDto) throws IOException;

    List<ProductDto> getAllProducts();
}
