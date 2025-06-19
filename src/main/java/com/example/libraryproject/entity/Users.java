package com.example.libraryproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", nullable = false, columnDefinition = "NVARCHAR(255)")
    String userId;
    @Column(name = "username", nullable = false, unique = true, columnDefinition = "NVARCHAR(255)")
    String username;
    @Column(name = "password", nullable = false, columnDefinition = "NVARCHAR(255)")
    String password;
    @Column(name = "full_name", nullable = false, columnDefinition = "NVARCHAR(255)")
    String fullName;
    @Column(name = "email", nullable = false, unique = true, columnDefinition = "NVARCHAR(255)")
    String email;
    @Column(name = "phone_number", columnDefinition = "NVARCHAR(20)")
    String phoneNumber;

    @Column(name = "role",columnDefinition = "NVARCHAR(50)")
    String role;
    @Column(name = "created_at", columnDefinition = "DATETIME")
    LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "is_active", columnDefinition = "bit")
    Boolean isActive = true;
}
