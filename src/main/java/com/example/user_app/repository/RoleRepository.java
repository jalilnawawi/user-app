package com.example.user_app.repository;

import com.example.user_app.model.ERole;
import com.example.user_app.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Role findByName(ERole name);
}
