package com.example.sepm_assignment.controller;

import com.example.sepm_assignment.dto.RegistrationRequest;
import com.example.sepm_assignment.model.User;
import com.example.sepm_assignment.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for AdminController
 * Tests HTTP requests/responses with full Spring context
 * Uses H2 in-memory database
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("AdminController Integration Tests")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User adminUser;
    private User testStudent;

    @BeforeEach
    void setUp() {
        // Clean database
        userRepository.deleteAll();

        // Create admin user
        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setEmail("admin@example.com");
        adminUser.setFullName("Admin User");
        adminUser.setRole(User.Role.ADMIN);
        adminUser.setEnabled(true);
        userRepository.save(adminUser);

        // Create test student
        testStudent = new User();
        testStudent.setUsername("student1");
        testStudent.setPassword(passwordEncoder.encode("password"));
        testStudent.setEmail("student1@example.com");
        testStudent.setFullName("Test Student");
        testStudent.setRole(User.Role.STUDENT);
        testStudent.setEnabled(true);
        userRepository.save(testStudent);
    }

    @Test
    @DisplayName("Should access create user form as admin")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void showCreateUserForm() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/admin/users/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-create-user"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @DisplayName("Should deny access to create user form for non-admin")
    @WithMockUser(username = "student1", roles = "STUDENT")
    void showCreateUserForm_AccessDenied() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/admin/users/create"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should create user successfully as admin")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createUser() throws Exception {
        // Arrange
        String username = "newteacher";
        String email = "newteacher@example.com";

        // Act & Assert
        mockMvc.perform(post("/admin/users/create")
                        .with(csrf())
                        .param("username", username)
                        .param("password", "password123")
                        .param("email", email)
                        .param("fullName", "New Teacher")
                        .param("role", "TEACHER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(flash().attributeExists("successMessage"))
                .andExpect(flash().attribute("successMessage",
                    containsString("User created successfully")));

        // Verify user was created
        assert(userRepository.existsByUsername(username));
    }

    @Test
    @DisplayName("Should fail to create user with duplicate username")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createUser_DuplicateUsername_Fails() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/admin/users/create")
                        .with(csrf())
                        .param("username", "student1") // Already exists
                        .param("password", "password123")
                        .param("email", "newemail@example.com")
                        .param("fullName", "New User")
                        .param("role", "STUDENT"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users/create"))
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attribute("errorMessage",
                    containsString("Username already exists")));
    }

    @Test
    @DisplayName("Should fail to create user with duplicate email")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createUser_DuplicateEmail_Fails() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/admin/users/create")
                        .with(csrf())
                        .param("username", "newuser")
                        .param("password", "password123")
                        .param("email", "student1@example.com") // Already exists
                        .param("fullName", "New User")
                        .param("role", "STUDENT"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users/create"))
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attribute("errorMessage",
                    containsString("Email already exists")));
    }

    @Test
    @DisplayName("Should delete user successfully as admin")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteUser() throws Exception {
        // Arrange
        Long userId = testStudent.getId();

        // Act & Assert
        mockMvc.perform(post("/admin/users/" + userId + "/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(flash().attributeExists("successMessage"))
                .andExpect(flash().attribute("successMessage",
                    containsString("User deleted successfully")));

        // Verify user was deleted
        assert(!userRepository.existsById(userId));
    }

    @Test
    @DisplayName("Should prevent admin from deleting themselves")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteUser_CannotDeleteSelf() throws Exception {
        // Arrange
        Long adminId = adminUser.getId();

        // Act & Assert
        mockMvc.perform(post("/admin/users/" + adminId + "/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attribute("errorMessage",
                    containsString("You cannot delete your own account")));

        // Verify admin was not deleted
        assert(userRepository.existsById(adminId));
    }

    @Test
    @DisplayName("Should toggle user status successfully")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void toggleUserStatus() throws Exception {
        // Arrange
        Long userId = testStudent.getId();
        boolean initialStatus = testStudent.isEnabled();

        // Act & Assert
        mockMvc.perform(post("/admin/users/" + userId + "/toggle-status")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(flash().attributeExists("successMessage"))
                .andExpect(flash().attribute("successMessage",
                    containsString("User status updated successfully")));

        // Verify status was toggled
        User updatedUser = userRepository.findById(userId).orElseThrow();
        assert(updatedUser.isEnabled() != initialStatus);
    }

    @Test
    @DisplayName("Should require CSRF token for POST requests")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createUser_RequiresCsrf() throws Exception {
        // Act & Assert - without CSRF token
        mockMvc.perform(post("/admin/users/create")
                        .param("username", "newuser")
                        .param("password", "password123")
                        .param("email", "newuser@example.com")
                        .param("fullName", "New User")
                        .param("role", "STUDENT"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should handle non-existent user deletion gracefully")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteUser_NotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/admin/users/999/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}

