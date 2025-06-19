package com.example.libraryproject.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Loans")
public class Loans {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "loan_id", nullable = false, columnDefinition = "NVARCHAR(255)")
    String loanId;
    
    @Column(name = "book_id", nullable = false, columnDefinition = "NVARCHAR(255)")
    String bookId;
    
    @Column(name = "user_id", nullable = false, columnDefinition = "NVARCHAR(255)")
    String userId;
    
    @Column(name = "name", columnDefinition = "NVARCHAR(255)")
    String name;
    @Column(name = "email",columnDefinition = "NVARCHAR(255)")
    String email;
    @Column(name = "phone_number", columnDefinition = "NVARCHAR(20)")
    String phoneNumber;
    @Column(name = "created_at", columnDefinition = "DATETIME")
    LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "fine", columnDefinition = "int")
    Integer fine;

}
