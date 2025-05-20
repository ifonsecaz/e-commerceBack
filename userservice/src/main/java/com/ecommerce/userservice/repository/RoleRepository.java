package com.ecommerce.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.userservice.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRole(String role);
}