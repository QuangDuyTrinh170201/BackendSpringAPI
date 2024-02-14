package com.example.BackendSpringAPI.repositories;

import com.example.BackendSpringAPI.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
//    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
//    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByEmail (String email);

    List<User> findAll();
}
