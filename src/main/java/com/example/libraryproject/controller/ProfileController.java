package com.example.libraryproject.controller;

import java.security.Principal;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.libraryproject.entity.Users;
import com.example.libraryproject.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class ProfileController {
    private final UserService userService;
    private static final String EDIT ="admin/profile/edit";
    private static final String ERROR = "Không tìm thấy thông tin người dùng";
    @GetMapping("/profile")
    public String showProfile(Principal principal, Model model) {
        String username = principal.getName();
        Users user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException(ERROR));
        
        model.addAttribute("user", user);
        return "admin/profile/view";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Principal principal, Model model) {
        String username = principal.getName();
        Users user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException(ERROR));
        
        model.addAttribute("user", user);
        return EDIT;
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("user") Users user,
                               BindingResult result,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        
        String currentUsername = principal.getName();
        Users currentUser = userService.getUserByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException(ERROR));

        if (result.hasErrors()) {
            return EDIT;
        }

        // Kiểm tra email trùng (trừ email hiện tại)
        if (userService.existsByEmailAndNotUserId(user.getEmail(), currentUser.getUserId())) {
            result.rejectValue("email", "error.user", "Email này đã được sử dụng");
            return EDIT;
        }

        try {
            // Cập nhật thông tin (không thay đổi username và role)
            currentUser.setFullName(user.getFullName());
            currentUser.setEmail(user.getEmail());
            currentUser.setPhoneNumber(user.getPhoneNumber());
            
            // Chỉ cập nhật password nếu có nhập mới
            if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
                currentUser.setPassword(user.getPassword());
            }

            userService.updateUser(currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
            return "redirect:/admin/profile";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/profile/edit";
        }
    }
} 