package com.example.libraryproject.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.libraryproject.entity.Books;
import com.example.libraryproject.service.BookService;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class BookController {

    private BookService bookService;
    private static final String BOOK = "book";
    private static final String BOOKS = "books";
    private static final String ERROR = "error";

    private static final String BOOK_VIEW = "admin/books/book";
    private static final String BOOK_VIEW2 = "redirect:/admin/books";
    private static final String BOOK_ADD = "admin/books/add";
    private static final String BOOK_EDIT = "admin/books/edit";

    private String handleInvalidBook(Books book, MultipartFile file, Model model,
                                     String errorMessage, Books existingBook, boolean isEdit) {
        model.addAttribute(ERROR, errorMessage);
        model.addAttribute(BOOK, book);

        if (isEdit) {
            if (file.isEmpty()) {
                book.setImageURL(existingBook.getImageURL());
            } else {
                String fileName = file.getOriginalFilename();
                book.setImageURL(fileName);
            }
            return BOOK_EDIT;
        } else {
            if (!file.isEmpty()) {
                String fileName = file.getOriginalFilename();
                book.setImageURL(fileName);
            }
            return BOOK_ADD;
        }
    }

    @RequestMapping("/books")
    public String index(Model model) {
        List<Books> list = this.bookService.getAll();
        model.addAttribute(BOOKS, list);
        return BOOK_VIEW;
    }

    @RequestMapping("/add-book")
    public String add(Model model) {
        Books book = new Books();
        model.addAttribute(BOOK, book);
        return BOOK_ADD;
    }

    @GetMapping("/books")
    public String showBooks(Model model) {
        List<Books> list = this.bookService.getAll();
        model.addAttribute(BOOKS, list);
        return BOOK_VIEW;
    }

    @PostMapping("/add-book")
    public String addBook(@ModelAttribute("book") Books book,
                          @RequestParam("bookImage") MultipartFile file,
                          Model model) {
        if (book.getQuantity() == null || book.getQuantity() <= 0 ||
                book.getName() == null || book.getName().trim().isEmpty() ||
                book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            return handleInvalidBook(book, file, model, "Thông tin sách không hợp lệ.", null, false);
        }
        if (bookService.existsByName(book.getName())) {
            return handleInvalidBook(book, file, model, "Sách đã tồn tại.", null, false);
        }
        
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            book.setImageURL(fileName);
        }
        
        if (Boolean.TRUE.equals(this.bookService.create(book))) {
            return BOOK_VIEW2;
        }
        return BOOK_ADD;
    }

    @GetMapping("/edit-book/{bookId}")
    public String editBook(Model model, @PathVariable("bookId") String bookId) {
        Books book = this.bookService.findById(bookId);
        model.addAttribute(BOOK, book);
        return BOOK_EDIT;
    }

    @PostMapping("/edit-book")
    public String updateBook(@ModelAttribute("book") Books book,
                             @RequestParam("bookImage") MultipartFile file,
                             Model model) {
        Books existingBook = this.bookService.findById(book.getBookId());
        if (book.getQuantity() == null || book.getQuantity() <= 0 ||
                book.getName() == null || book.getName().trim().isEmpty() ||
                book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            return handleInvalidBook(book, file, model, "Thông tin sách không hợp lệ.", existingBook, true);
        }
        if (!existingBook.getName().equals(book.getName()) &&
                bookService.existsByName(book.getName())) {
            return handleInvalidBook(book, file, model, "Sách đã tồn tại.", existingBook, true);
        }

        String fileName;
        if (file.isEmpty()) {
            fileName = existingBook.getImageURL();
            book.setImageURL(fileName);
        } else {
            fileName = file.getOriginalFilename();
            book.setImageURL(fileName);
        }

        if (Boolean.TRUE.equals(this.bookService.create(book))) {
            return BOOK_VIEW2;
        } else {
            return BOOK_EDIT;
        }
    }

    @GetMapping("/delete-book/{bookId}")
    public String deleteBook(@PathVariable("bookId") String bookId) {
        if (Boolean.TRUE.equals(this.bookService.delete(bookId))) {
            return BOOK_VIEW2;
        } else {
            return BOOK_VIEW;
        }
    }
}
