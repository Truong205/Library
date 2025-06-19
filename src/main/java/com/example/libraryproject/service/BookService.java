package com.example.libraryproject.service;

import java.util.List;
import com.example.libraryproject.entity.Books;
import com.example.libraryproject.repository.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@AllArgsConstructor
public class BookService {
    private final BookRepository repo;
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);
    private static final String ERROR = "Error creating book: {}";
    
    public List<Books> getAll() {
        return this.repo.findAll();
    }

    public Boolean create(Books book) {
        try {
            this.repo.save(book);
            return true;
        } catch (Exception e) {
            logger.error(ERROR, e.getMessage(), e);
        }
        return false;
    }

    public Books findById(String bookId) {
        return this.repo.findById(bookId).orElse(null);
    }

    public Boolean delete(String bookId) {
        try {
            this.repo.deleteById(bookId);
            return true;
        } catch (Exception e) {
            logger.error(ERROR, e.getMessage(), e);
        }
        return false;
    }

    public boolean existsByName(String name) {
        return repo.existsByName(name);
    }

    public List<String> getDistinctAuthors() {
        return repo.findDistinctAuthors();
    }
}
