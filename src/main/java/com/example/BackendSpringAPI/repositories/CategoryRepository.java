package com.example.BackendSpringAPI.repositories;

import com.example.BackendSpringAPI.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
