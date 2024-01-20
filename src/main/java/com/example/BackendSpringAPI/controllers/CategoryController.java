package com.example.BackendSpringAPI.controllers;

import com.example.BackendSpringAPI.dtos.CategoryDTO;
import com.example.BackendSpringAPI.models.Category;
import com.example.BackendSpringAPI.models.Product;
import com.example.BackendSpringAPI.responses.ProductResponse;
import com.example.BackendSpringAPI.responses.UpdateCategoryResponse;
import com.example.BackendSpringAPI.services.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    @PostMapping("")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDTO categoryDTO, BindingResult result){
        if(result.hasErrors()){
            List<String> errorMessage = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
            return ResponseEntity.badRequest().body(errorMessage);
        }
        categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok("Insert category successfully!");
    }

    //Hiển thị tất cả categories
    @GetMapping("/getAll")
    public ResponseEntity<List<Category>> getAllCategories(){
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryWithId(@PathVariable("id") Long categoryId){
        try {
            Category existingCategory = categoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(existingCategory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<UpdateCategoryResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO, HttpServletRequest request){
        categoryService.updateCategory(id, categoryDTO);
        Locale locale = localeResolver.resolveLocale(request);
        return ResponseEntity.ok(UpdateCategoryResponse.builder()
                        .message(messageSource.getMessage("category.update_category.update_successfully", null, locale))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Delete category with id = " + id + " successfully!");
    }
}
