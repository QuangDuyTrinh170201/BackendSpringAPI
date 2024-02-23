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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import java.util.*;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    @PostMapping("")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDTO categoryDTO, BindingResult result){
        try{
            if(result.hasErrors()){
                List<String> errorMessage = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMessage);
            }
            categoryService.createCategory(categoryDTO);
            return ResponseEntity.ok("Insert category successfully!");
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
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
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO, HttpServletRequest request) {
        try {
            categoryService.updateCategory(id, categoryDTO);
            Locale locale = localeResolver.resolveLocale(request);
            String message = messageSource.getMessage("category.update_category.update_successfully", null, locale);
            return ResponseEntity.ok(Collections.singletonMap("message", message));
        } catch (RuntimeException e) {
            // Xử lý ngoại lệ và trả về một phản hồi lỗi
            Locale locale = localeResolver.resolveLocale(request);
            String errorMessage = messageSource.getMessage("category.update_category.update_failed", null, locale);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Delete category with id = " + id + " successfully!");
    }
}
