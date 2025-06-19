package com.example.libraryproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.libraryproject.entity.Books;

public interface BookRepository extends JpaRepository<Books, String> {
    boolean existsByName(String name);
    
    @Query("SELECT DISTINCT b.author FROM Books b")
    List<String> findDistinctAuthors();
}
