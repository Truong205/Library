package com.example.libraryproject.controller;

import java.util.List;

import com.example.libraryproject.entity.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.libraryproject.entity.Books;
import com.example.libraryproject.entity.Loans;
import com.example.libraryproject.entity.Users;
import com.example.libraryproject.service.BookService;
import com.example.libraryproject.service.LoanService;
import com.example.libraryproject.repository.UserRepository;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;
    private final UserRepository userRepository;
    
    private static final String LOAN = "loan";
    private static final String LOANS = "loans";
    private static final String ERROR = "error";
    private static final String BOOKS = "books";
    private static final String USERS = "users";
    private static final Role LIBRARIAN = Role.LIBRARIAN;
    private static final String LOAN_VIEW = "admin/loans/loan";
    private static final String LOAN_VIEW2 = "redirect:/admin/loans";
    private static final String LOAN_ADD = "admin/loans/add";
    private static final String LOAN_EDIT = "admin/loans/edit";

    @GetMapping("/loans")
    public String index(Model model) {
        List<Loans> list = loanService.getAll();
        model.addAttribute(LOANS, list);
        return LOAN_VIEW;
    }

    @GetMapping("/add-loan")
    public String add(Model model) {
        Loans loan = new Loans();
        List<Books> booksList = bookService.getAll();
        List<Users> usersList = userRepository.findByRole(LIBRARIAN);
        
        model.addAttribute(LOAN, loan);
        model.addAttribute(BOOKS, booksList);
        model.addAttribute(USERS, usersList);
        return LOAN_ADD;
    }

    @PostMapping("/add-loan")
    public String addLoan(@ModelAttribute("loan") Loans loan, Model model) {
        // Validation
        if (loan.getName() == null || loan.getName().trim().isEmpty() ||
        !loan.getName().matches("^[\\p{L}\\s]+$")) {
            return handleInvalidLoan(loan, model, "Tên người mượn không được để trống.");
        }
        
        if (loan.getEmail() == null || loan.getEmail().trim().isEmpty() || 
            !loan.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return handleInvalidLoan(loan, model, "Email không hợp lệ.");
        }
        
        if (loan.getPhoneNumber() == null || loan.getPhoneNumber().trim().isEmpty() ||
            !loan.getPhoneNumber().matches("\\d{10,11}$")) {
            return handleInvalidLoan(loan, model, "Số điện thoại không hợp lệ (10-11 chữ số).");
        }
        
        if (loan.getBookId() == null || loan.getBookId().trim().isEmpty()) {
            return handleInvalidLoan(loan, model, "Vui lòng chọn sách.");
        }
        
        if (loan.getUserId() == null || loan.getUserId().trim().isEmpty()) {
            return handleInvalidLoan(loan, model, "Vui lòng chọn thủ thư.");
        }
        
        // Check if book is available
        if (!loanService.isBookAvailable(loan.getBookId())) {
            return handleInvalidLoan(loan, model, "Sách này hiện không còn bản nào để cho mượn.");
        }
        
        if (Boolean.TRUE.equals(loanService.create(loan))) {
            return LOAN_VIEW2;
        }
        
        return handleInvalidLoan(loan, model, "Lỗi khi tạo phiếu mượn.");
    }

    @GetMapping("/edit-loan/{loanId}")
    public String editLoan(Model model, @PathVariable("loanId") String loanId) {
        Loans loan = loanService.findById(loanId);
        if (loan == null) {
            return LOAN_VIEW2;
        }
        
        List<Books> booksList = bookService.getAll();
        List<Users> usersList = userRepository.findByRole(LIBRARIAN);
        
        model.addAttribute(LOAN, loan);
        model.addAttribute(BOOKS, booksList);
        model.addAttribute(USERS, usersList);
        return LOAN_EDIT;
    }

    @PostMapping("/edit-loan")
    public String updateLoan(@ModelAttribute("loan") Loans loan, Model model) {
        // Validation similar to add
        if (loan.getName() == null || loan.getName().trim().isEmpty() ||
        !loan.getName().matches("^[\\p{L}\\s]+$")) {
            return handleInvalidLoan(loan, model, "Tên người mượn không được để trống.");
        }
        
        if (loan.getEmail() == null || loan.getEmail().trim().isEmpty() || 
            !loan.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return handleInvalidLoan(loan, model, "Email không hợp lệ.");
        }
        
        if (loan.getPhoneNumber() == null || loan.getPhoneNumber().trim().isEmpty() ||
            !loan.getPhoneNumber().matches("\\d{10,11}$")) {
            return handleInvalidLoan(loan, model, "Số điện thoại không hợp lệ (10-11 chữ số).");
        }
        
        if (Boolean.TRUE.equals(loanService.update(loan))) {
            return LOAN_VIEW2;
        }
        
        return LOAN_EDIT;
    }

    @GetMapping("/return-loan/{loanId}")
    public String returnBook(@PathVariable("loanId") String loanId) {
        if (Boolean.TRUE.equals(loanService.returnBook(loanId))) {
            return LOAN_VIEW2;
        }
        return LOAN_VIEW;
    }

    @GetMapping("/extend-loan/{loanId}")
    public String extendLoan(@PathVariable("loanId") String loanId) {
        if (Boolean.TRUE.equals(loanService.extendLoan(loanId))) {
            return LOAN_VIEW2;
        }
        return LOAN_VIEW;
    }

    @GetMapping("/delete-loan/{loanId}")
    public String deleteLoan(@PathVariable("loanId") String loanId) {
        if (Boolean.TRUE.equals(loanService.delete(loanId))) {
            return LOAN_VIEW2;
        }
        return LOAN_VIEW;
    }

    private String handleInvalidLoan(Loans loan, Model model, String errorMessage) {
        List<Books> booksList = bookService.getAll();
        List<Users> usersList = userRepository.findByRole(LIBRARIAN);
        
        model.addAttribute(ERROR, errorMessage);
        model.addAttribute(LOAN, loan);
        model.addAttribute(BOOKS, booksList);
        model.addAttribute(USERS, usersList);
        
        return loan.getLoanId() != null ? LOAN_EDIT : LOAN_ADD;
    }
} 