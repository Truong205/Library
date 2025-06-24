package com.example.libraryproject.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.libraryproject.entity.Role;
import com.example.libraryproject.entity.Users;
import com.example.libraryproject.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    // Constants
    private static final String USER = "user";
    private static final String USERS = "users";
    private static final String ERROR = "error";
    
    private static final String USER_LIST_VIEW = "admin/users/list";
    private static final String USER_LIST_REDIRECT = "redirect:/admin/users";
    private static final String USER_ADD_VIEW = "admin/users/add";
    private static final String USER_EDIT_VIEW = "admin/users/edit";
    private static final String USER_VIEW_DETAIL = "admin/users/view";
    
    // Helper method for handling invalid user (similar to BookController)
    private String handleInvalidUser(Users user, Model model, String errorMessage, boolean isEdit) {
        model.addAttribute(ERROR, errorMessage);
        model.addAttribute(USER, user);
        model.addAttribute("roles", Role.values());
        
        return isEdit ? USER_EDIT_VIEW : USER_ADD_VIEW;
    }
    
    @RequestMapping("/users")
    public String index(Model model) {
        List<Users> list = userService.getAllUsers();
        model.addAttribute(USERS, list);
        return USER_LIST_VIEW;
    }
    
    @RequestMapping("/add-user")
    public String add(Model model) {
        Users user = new Users();
        model.addAttribute(USER, user);
        model.addAttribute("roles", Role.values());
        return USER_ADD_VIEW;
    }
    
    @GetMapping("/users")
    public String showUsers(Model model) {
        List<Users> list = userService.getAllUsers();
        model.addAttribute(USERS, list);
        return USER_LIST_VIEW;
    }
    
    @PostMapping("/add-user")
    public String addUser(@Valid @ModelAttribute(USER) Users user, 
                         BindingResult bindingResult, 
                         Model model) {
        
        // Basic validation
        if (user.getUsername() == null || user.getUsername().trim().isEmpty() ||
            user.getPassword() == null || user.getPassword().trim().isEmpty() ||
            user.getFullName() == null || user.getFullName().trim().isEmpty() ||
            user.getEmail() == null || user.getEmail().trim().isEmpty() ||
            user.getRole() == null) {
            return handleInvalidUser(user, model, "Thông tin người dùng không hợp lệ.", false);
        }
        
        if (userService.existsByUsername(user.getUsername())) {
            return handleInvalidUser(user, model, "Tên đăng nhập đã tồn tại.", false);
        }
        
        if (userService.existsByEmail(user.getEmail())) {
            return handleInvalidUser(user, model, "Email đã tồn tại.", false);
        }
        
        Users savedUser = userService.saveUser(user);
        if (savedUser != null) {
            return USER_LIST_REDIRECT;
        }
        return USER_ADD_VIEW;
    }
    
    @GetMapping("/edit-user/{userId}")
    public String editUser(Model model, @PathVariable("userId") String userId) {
        Users user = userService.getUserById(userId).orElse(null);
        if (user == null) {
            return USER_LIST_REDIRECT;
        }
        model.addAttribute(USER, user);
        model.addAttribute("roles", Role.values());
        return USER_EDIT_VIEW;
    }
    
    @PostMapping("/edit-user")
    public String updateUser(@Valid @ModelAttribute(USER) Users user, 
                           BindingResult bindingResult, 
                           Model model) {
        
        Users existingUser = userService.getUserById(user.getUserId()).orElse(null);
        if (existingUser == null) {
            return USER_LIST_REDIRECT;
        }
        
        // Basic validation
        if (user.getFullName() == null || user.getFullName().trim().isEmpty() ||
            user.getEmail() == null || user.getEmail().trim().isEmpty() ||
            user.getRole() == null) {
            return handleInvalidUser(user, model, "Thông tin người dùng không hợp lệ.", true);
        }
        
        // Check username uniqueness (excluding current user)
        if (!existingUser.getUsername().equals(user.getUsername()) &&
            userService.existsByUsername(user.getUsername())) {
            return handleInvalidUser(user, model, "Tên đăng nhập đã tồn tại.", true);
        }
        
        // Check email uniqueness (excluding current user)
        if (!existingUser.getEmail().equals(user.getEmail()) &&
            userService.existsByEmail(user.getEmail())) {
            return handleInvalidUser(user, model, "Email đã tồn tại.", true);
        }
        
        try {
            Users updatedUser = userService.updateUser(user);
            if (updatedUser != null) {
                return USER_LIST_REDIRECT;
            }
        } catch (Exception e) {
            return handleInvalidUser(user, model, "Có lỗi xảy ra: " + e.getMessage(), true);
        }
        
        return USER_EDIT_VIEW;
    }
    
    @GetMapping("/view-user/{userId}")
    public String viewUser(Model model, @PathVariable("userId") String userId) {
        Users user = userService.getUserById(userId).orElse(null);
        if (user == null) {
            return USER_LIST_REDIRECT;
        }
        model.addAttribute(USER, user);
        return USER_VIEW_DETAIL;
    }
    
    @GetMapping("/delete-user/{userId}")
    public String deleteUser(@PathVariable("userId") String userId) {
        try {
            userService.deleteUser(userId);
            return USER_LIST_REDIRECT;
        } catch (Exception e) {
            return USER_LIST_VIEW;
        }
    }
    
    @GetMapping("/deactivate-user/{userId}")
    public String deactivateUser(@PathVariable("userId") String userId) {
        try {
            userService.deactivateUser(userId);
            return USER_LIST_REDIRECT;
        } catch (Exception e) {
            return USER_LIST_VIEW;
        }
    }
    
    @GetMapping("/reactivate-user/{userId}")
    public String reactivateUser(@PathVariable("userId") String userId) {
        try {
            userService.reactivateUser(userId);
            return USER_LIST_REDIRECT;
        } catch (Exception e) {
            return USER_LIST_VIEW;
        }
    }
} 