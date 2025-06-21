package com.example.libraryproject.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.libraryproject.entity.Books;
import com.example.libraryproject.entity.Loans;
import com.example.libraryproject.repository.BookRepository;
import com.example.libraryproject.repository.LoanRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private static final Logger logger = LoggerFactory.getLogger(LoanService.class);
    private static final int FINE_PER_PERIOD = 30000; // 30000 VND per 7 days
    private static final int LOAN_PERIOD_DAYS = 7;

    public List<Loans> getAll() {
        List<Loans> loans = loanRepository.findAll();
        // Update fine for all loans
        loans.forEach(this::updateFine);
        return loans;
    }

    public List<Loans> getActiveLoans() {
        List<Loans> loans = loanRepository.findByIsReturnedFalse();
        loans.forEach(this::updateFine);
        return loans;
    }

    public List<Loans> getReturnedLoans() {
        return loanRepository.findByIsReturnedTrue();
    }

    public Loans findById(String loanId) {
        Loans loan = loanRepository.findById(loanId).orElse(null);
        if (loan != null && !loan.getIsReturned()) {
            updateFine(loan);
        }
        return loan;
    }

    @Transactional
    public Boolean create(Loans loan) {
        try {
            // Check if book is available
            Books book = bookRepository.findById(loan.getBookId()).orElse(null);
            if (book == null) {
                logger.error("Book not found with id: {}", loan.getBookId());
                return false;
            }

            // Check available quantity
            int activeLoans = loanRepository.countActiveLoansForBook(loan.getBookId());
            if (book.getQuantity() <= activeLoans) {
                logger.error("No available copies for book: {}", book.getName());
                return false;
            }

            // Set default values
            loan.setCreatedAt(LocalDateTime.now());
            loan.setLimitation(LocalDateTime.now().plusDays(LOAN_PERIOD_DAYS));
            loan.setIsReturned(false);
            loan.setFine(0);

            loanRepository.save(loan);
            return true;
        } catch (Exception e) {
            logger.error("Error creating loan: {}", e.getMessage(), e);
            return false;
        }
    }

    @Transactional
    public Boolean update(Loans loan) {
        try {
            Loans existingLoan = loanRepository.findById(loan.getLoanId()).orElse(null);
            if (existingLoan == null) {
                return false;
            }

            // Update fine before saving
            if (!loan.getIsReturned()) {
                updateFine(loan);
            }

            loanRepository.save(loan);
            return true;
        } catch (Exception e) {
            logger.error("Error updating loan: {}", e.getMessage(), e);
            return false;
        }
    }

    @Transactional
    public Boolean returnBook(String loanId) {
        try {
            Loans loan = loanRepository.findById(loanId).orElse(null);
            if (loan == null || loan.getIsReturned()) {
                return false;
            }

            // Calculate final fine
            updateFine(loan);
            loan.setIsReturned(true);
            loan.setReturnedDate(LocalDateTime.now());

            loanRepository.save(loan);
            return true;
        } catch (Exception e) {
            logger.error("Error returning book: {}", e.getMessage(), e);
            return false;
        }
    }

    @Transactional
    public Boolean extendLoan(String loanId) {
        try {
            Loans loan = loanRepository.findById(loanId).orElse(null);
            if (loan == null || loan.getIsReturned()) {
                return false;
            }

            // Extend limitation by 7 days
            loan.setLimitation(loan.getLimitation().plusDays(LOAN_PERIOD_DAYS));
            loanRepository.save(loan);
            return true;
        } catch (Exception e) {
            logger.error("Error extending loan: {}", e.getMessage(), e);
            return false;
        }
    }

    @Transactional
    public Boolean delete(String loanId) {
        try {
            loanRepository.deleteById(loanId);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting loan: {}", e.getMessage(), e);
            return false;
        }
    }

    // Calculate fine based on overdue days
    private void updateFine(Loans loan) {
        if (loan.getIsReturned() || loan.getLimitation() == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(loan.getLimitation())) {
            long overdueDays = ChronoUnit.DAYS.between(loan.getLimitation(), now);
            int periods = (int) Math.ceil((double) overdueDays / LOAN_PERIOD_DAYS);
            loan.setFine(periods * FINE_PER_PERIOD);
        } else {
            loan.setFine(0);
        }
    }

    // Scheduled task to update fines daily
    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
    public void updateAllFines() {
        List<Loans> overdueLoans = loanRepository.findOverdueLoans();
        overdueLoans.forEach(loan -> {
            updateFine(loan);
            loanRepository.save(loan);
        });
    }

    public boolean isBookAvailable(String bookId) {
        Books book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            return false;
        }
        int activeLoans = loanRepository.countActiveLoansForBook(bookId);
        return book.getQuantity() > activeLoans;
    }

    public List<Loans> getLoansByUserId(String userId) {
        List<Loans> loans = loanRepository.findByUserId(userId);
        loans.forEach(this::updateFine);
        return loans;
    }
} 