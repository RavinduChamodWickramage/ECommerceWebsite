package com.practice.ecommerce.service.admin.category;

import com.practice.ecommerce.dto.CategoryDto;
import com.practice.ecommerce.entity.Category;

import java.util.List;

public interface CategoryService {

    Category createCategory(CategoryDto categoryDto);

    List<Category> getAllCategories();
}
