package com.example.BackendSpringAPI.repositories;

import com.example.BackendSpringAPI.models.Category;
import com.example.BackendSpringAPI.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.*;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);
    Page<Product> findAll(Pageable pageable);
    Page<Product> findByCategory(Category category, Pageable pageable);
}
