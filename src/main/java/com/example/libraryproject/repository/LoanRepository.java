package com.example.libraryproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.libraryproject.entity.Loans;

@Repository
public interface LoanRepository extends JpaRepository<Loans, String> {
    List<Loans> findByUserId(String userId);
    
    List<Loans> findByBookId(String bookId);
    
    List<Loans> findByIsReturnedFalse();
    
    List<Loans> findByIsReturnedTrue();
    
    @Query("SELECT l FROM Loans l WHERE l.isReturned = false AND l.limitation < CURRENT_TIMESTAMP")
    List<Loans> findOverdueLoans();
    
    boolean existsByBookIdAndIsReturnedFalse(String bookId);
    
    @Query("SELECT COUNT(l) FROM Loans l WHERE l.bookId = ?1 AND l.isReturned = false")
    int countActiveLoansForBook(String bookId);
} 