package com.example.BackendSpringAPI.controllers;

import com.example.BackendSpringAPI.dtos.CategoryDTO;
import com.example.BackendSpringAPI.models.Category;
import com.example.BackendSpringAPI.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
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
    @GetMapping("")
    public ResponseEntity<List<Category>> getAllCategories(@RequestParam("page") int page, @RequestParam("limit") int limit){
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }


    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO){
        categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok("Update category successfully!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Delete category with id = " + id + " successfully!");
    }
}
