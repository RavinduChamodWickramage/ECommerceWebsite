package com.practice.ecommerce.service.admin.product;

import com.practice.ecommerce.dto.ProductDto;

import java.io.IOException;
import java.util.List;

public interface AdminProductService {

    ProductDto addProduct(ProductDto productDto) throws IOException;

    List<ProductDto> getAllProducts();

    List<ProductDto> searchProductByName(String name);

    boolean deleteProduct(Long id);
}
