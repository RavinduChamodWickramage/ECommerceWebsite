package com.practice.ecommerce.controller.customer;

import com.practice.ecommerce.dto.ProductDto;
import com.practice.ecommerce.service.customer.CustomerProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerProductController {

    private final CustomerProductService customerProductService;

    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> productDtos = customerProductService.getAllProducts();
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<ProductDto>> getAllProductByName(@PathVariable String name) {
        List<ProductDto> productDtos = customerProductService.searchProductByName(name);
        return ResponseEntity.ok(productDtos);
    }
}
