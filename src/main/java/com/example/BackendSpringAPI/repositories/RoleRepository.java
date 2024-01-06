package com.example.BackendSpringAPI.repositories;

import com.example.BackendSpringAPI.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
