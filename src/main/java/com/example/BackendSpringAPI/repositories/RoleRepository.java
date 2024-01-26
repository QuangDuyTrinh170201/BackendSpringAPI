package com.example.BackendSpringAPI.repositories;

import com.example.BackendSpringAPI.models.Role;
import com.example.BackendSpringAPI.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
