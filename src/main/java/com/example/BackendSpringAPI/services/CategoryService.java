package com.example.BackendSpringAPI.services;

import com.example.BackendSpringAPI.dtos.CategoryDTO;
import com.example.BackendSpringAPI.models.Category;
import com.example.BackendSpringAPI.repositories.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService{
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Category createCategory(CategoryDTO categoryDTO) {
        Optional<Category> existingCategory = categoryRepository.findByName(categoryDTO.getName());
        if(existingCategory.isPresent()) {
            throw new RuntimeException("A category with the same name already exists.");
        }
        Category newCategory = Category.builder()
                .name(categoryDTO.getName()).build();
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found!"));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public Category updateCategory(long categoryId, CategoryDTO categoryDTO) {
        Category existingCategory = getCategoryById(categoryId);
        existingCategory.setName(categoryDTO.getName());
        categoryRepository.save(existingCategory);
        return  existingCategory;
    }

    @Override
    @Transactional
    public void deleteCategory(long id) {
        //xóa cứng
        categoryRepository.deleteById(id);
    }
}
