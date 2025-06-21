package com.example.libraryproject.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.libraryproject.entity.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, String> {
    List<Users> findByRole(String role);
    Users findByUsername(String username);
} 