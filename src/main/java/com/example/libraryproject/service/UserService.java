package com.example.libraryproject.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.libraryproject.entity.Users;
import com.example.libraryproject.repository.UserRepository;

@Service
@AllArgsConstructor
public class UserService {
    
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<Users> getUserById(String userId) {
        return userRepository.findById(userId);
    }
    
    public Optional<Users> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<Users> searchUsersByName(String name) {
        return userRepository.findByFullNameContainingIgnoreCase(name);
    }
    
    public Users saveUser(Users user) {
        if (user.getUserId() == null) {
            user.setCreatedAt(LocalDateTime.now());
            user.setIsActive(true);
        }
        
        return userRepository.save(user);
    }
    
    public Users updateUser(Users user) {
        Optional<Users> existingUser = userRepository.findById(user.getUserId());
        if (existingUser.isPresent()) {
            Users existing = existingUser.get();
            
            // Chỉ cập nhật các field được phép
            existing.setFullName(user.getFullName());
            existing.setEmail(user.getEmail());
            existing.setPhoneNumber(user.getPhoneNumber());
            existing.setRole(user.getRole());
            existing.setIsActive(user.getIsActive());
            
            // Chỉ cập nhật password nếu có password mới
            if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
                existing.setPassword(user.getPassword()); // Lưu plain text
            }
            
            return userRepository.save(existing);
        }
        throw new RuntimeException("Không tìm thấy người dùng với ID: " + user.getUserId());
    }
    
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
    
    public void deactivateUser(String userId) {
        Optional<Users> user = userRepository.findById(userId);
        if (user.isPresent()) {
            user.get().setIsActive(false);
            userRepository.save(user.get());
        }
    }
    
    public void reactivateUser(String userId) {
        Optional<Users> user = userRepository.findById(userId);
        if (user.isPresent()) {
            user.get().setIsActive(true);
            userRepository.save(user.get());
        }
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
    

    
    public boolean existsByEmailAndNotUserId(String email, String userId) {
        Optional<Users> user = userRepository.findByEmail(email);
        return user.isPresent() && !user.get().getUserId().equals(userId);
    }
} 