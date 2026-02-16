package com.example.sepm_assignment.repository;

import com.example.sepm_assignment.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository Tests for UserRepository
 * Tests data access layer with H2 in-memory database
 * Uses @DataJpaTest for lightweight testing
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testStudent;
    private User testTeacher;

    @BeforeEach
    void setUp() {
        // Clean database
        userRepository.deleteAll();

        // Create test student
        testStudent = new User();
        testStudent.setUsername("student1");
        testStudent.setPassword("password");
        testStudent.setEmail("student1@example.com");
        testStudent.setFullName("Test Student");
        testStudent.setRole(User.Role.STUDENT);
        testStudent.setEnabled(true);
        userRepository.save(testStudent);

        // Create test teacher
        testTeacher = new User();
        testTeacher.setUsername("teacher1");
        testTeacher.setPassword("password");
        testTeacher.setEmail("teacher1@example.com");
        testTeacher.setFullName("Test Teacher");
        testTeacher.setRole(User.Role.TEACHER);
        testTeacher.setEnabled(true);
        userRepository.save(testTeacher);
    }

    @Test
    @DisplayName("Should save user successfully")
    void save() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password");
        newUser.setEmail("newuser@example.com");
        newUser.setFullName("New User");
        newUser.setRole(User.Role.STUDENT);
        newUser.setEnabled(true);

        // Act
        User savedUser = userRepository.save(newUser);

        // Assert
        assertNotNull(savedUser.getId());
        assertEquals("newuser", savedUser.getUsername());
    }

    @Test
    @DisplayName("Should find user by ID successfully")
    void findById() {
        // Act
        Optional<User> found = userRepository.findById(testStudent.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("student1", found.get().getUsername());
    }

    @Test
    @DisplayName("Should return empty when user not found by ID")
    void findById_NotFound() {
        // Act
        Optional<User> found = userRepository.findById(999L);

        // Assert
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should find user by username successfully")
    void findByUsername() {
        // Act
        Optional<User> found = userRepository.findByUsername("student1");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("student1@example.com", found.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty when username not found")
    void findByUsername_NotFound() {
        // Act
        Optional<User> found = userRepository.findByUsername("nonexistent");

        // Assert
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should check if username exists")
    void existsByUsername() {
        // Act & Assert
        assertTrue(userRepository.existsByUsername("student1"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    @DisplayName("Should check if email exists")
    void existsByEmail() {
        // Act & Assert
        assertTrue(userRepository.existsByEmail("student1@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    @DisplayName("Should find users by role")
    void findByRole() {
        // Act
        List<User> students = userRepository.findByRole(User.Role.STUDENT);
        List<User> teachers = userRepository.findByRole(User.Role.TEACHER);

        // Assert
        assertEquals(1, students.size());
        assertEquals(1, teachers.size());
        assertEquals("student1", students.get(0).getUsername());
        assertEquals("teacher1", teachers.get(0).getUsername());
    }

    @Test
    @DisplayName("Should delete user by ID")
    void deleteById() {
        // Arrange
        Long userId = testStudent.getId();

        // Act
        userRepository.deleteById(userId);

        // Assert
        assertFalse(userRepository.existsById(userId));
    }

    @Test
    @DisplayName("Should find all users")
    void findAll() {
        // Act
        List<User> users = userRepository.findAll();

        // Assert
        assertEquals(2, users.size());
    }

    @Test
    @DisplayName("Should update user successfully")
    void update() {
        // Arrange
        User user = userRepository.findById(testStudent.getId()).orElseThrow();
        user.setFullName("Updated Name");

        // Act
        User updated = userRepository.save(user);

        // Assert
        assertEquals("Updated Name", updated.getFullName());
    }

    @Test
    @DisplayName("Should enforce unique username constraint")
    void save_DuplicateUsername_ThrowsException() {
        // Arrange
        User duplicateUser = new User();
        duplicateUser.setUsername("student1"); // Duplicate
        duplicateUser.setPassword("password");
        duplicateUser.setEmail("different@example.com");
        duplicateUser.setFullName("Different User");
        duplicateUser.setRole(User.Role.STUDENT);
        duplicateUser.setEnabled(true);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            userRepository.save(duplicateUser);
            userRepository.flush(); // Force constraint check
        });
    }

    @Test
    @DisplayName("Should handle transactional behavior correctly")
    void transactionalBehavior() {
        // Arrange
        long initialCount = userRepository.count();

        // Act
        User user = new User();
        user.setUsername("transactional");
        user.setPassword("password");
        user.setEmail("transactional@example.com");
        user.setFullName("Transactional User");
        user.setRole(User.Role.STUDENT);
        user.setEnabled(true);
        userRepository.save(user);

        // Assert
        assertEquals(initialCount + 1, userRepository.count());
    }
}
