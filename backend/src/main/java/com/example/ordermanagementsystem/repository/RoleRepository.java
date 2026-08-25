package com.example.ordermanagementsystem.repository;

import com.example.ordermanagementsystem.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByRole(String role);
    Optional<Role> findByRole(String role);
}