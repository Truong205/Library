package com.example.libraryproject.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.libraryproject.entity.Users;
import com.example.libraryproject.entity.Role;

@Repository
public interface UserRepository extends JpaRepository<Users, String> {
    List<Users> findByRole(Role role);
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
    List<Users> findByIsActiveTrue();
    List<Users> findByFullNameContainingIgnoreCase(String fullName);
} 