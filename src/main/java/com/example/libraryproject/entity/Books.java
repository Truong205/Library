package com.example.libraryproject.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Books")
public class Books {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "book_id", nullable = false, columnDefinition = "NVARCHAR(255)")
    String bookId;

    @Column(name = "name", nullable = false, columnDefinition = "NVARCHAR(255)")
    String name;

    @Column(name = "author", nullable = false, columnDefinition = "NVARCHAR(255)")
    String author;

    @Column(name = "quantity", columnDefinition = "int")
    Integer quantity;

    @Column(name = "imageurl", columnDefinition = "NVARCHAR(255)")
    String imageURL;

    @Column(name = "is_active", columnDefinition = "bit")
    Boolean isActive = true;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME")
    LocalDateTime createdAt = LocalDateTime.now();
}
