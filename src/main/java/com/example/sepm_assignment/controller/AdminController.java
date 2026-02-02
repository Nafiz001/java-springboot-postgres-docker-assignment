package com.example.sepm_assignment.controller;

import com.example.sepm_assignment.dto.RegistrationRequest;
import com.example.sepm_assignment.dto.UserDTO;
import com.example.sepm_assignment.model.User;
import com.example.sepm_assignment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new RegistrationRequest());
        return "admin-create-user";
    }

    @PostMapping("/users/create")
    public String createUser(@ModelAttribute RegistrationRequest request,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(request);
            redirectAttributes.addFlashAttribute("successMessage",
                "User created successfully: " + request.getUsername());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Failed to create user: " + e.getMessage());
            return "redirect:/admin/users/create";
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails currentUser,
                             RedirectAttributes redirectAttributes) {
        try {
            UserDTO user = userService.getUserById(id);
            if (user.getUsername().equals(currentUser.getUsername())) {
                redirectAttributes.addFlashAttribute("errorMessage",
                    "You cannot delete your own account!");
                return "redirect:/admin/dashboard";
            }
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage",
                "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Failed to delete user: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id,
                                    RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("successMessage",
                "User status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Failed to update user status: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}
