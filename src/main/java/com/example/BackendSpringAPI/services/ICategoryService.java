package com.example.BackendSpringAPI.services;

import com.example.BackendSpringAPI.dtos.CategoryDTO;
import com.example.BackendSpringAPI.models.Category;

import java.util.List;

public interface ICategoryService {
    Category createCategory(CategoryDTO category);
    Category getCategoryById(long id);
    List<Category> getAllCategories();
    Category updateCategory(long categoryId, CategoryDTO category);
    void deleteCategory(long id);
}
