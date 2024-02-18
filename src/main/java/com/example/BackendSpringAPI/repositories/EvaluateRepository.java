package com.example.BackendSpringAPI.repositories;

import com.example.BackendSpringAPI.models.Evaluate;
import com.example.BackendSpringAPI.models.Product;
import com.example.BackendSpringAPI.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluateRepository extends JpaRepository<Evaluate, Long> {
    List<Evaluate> findByUser(User user);
    List<Evaluate> findByProduct(Product product);

}

