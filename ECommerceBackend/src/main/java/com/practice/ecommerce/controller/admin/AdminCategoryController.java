package com.practice.ecommerce.controller.admin;

import com.practice.ecommerce.dto.CategoryDto;
import com.practice.ecommerce.entity.Category;
import com.practice.ecommerce.service.admin.category.AdminCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    @PostMapping("/category")
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDto categoryDto) {
        Category category = adminCategoryService.createCategory(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(adminCategoryService.getAllCategories());
    }

}
