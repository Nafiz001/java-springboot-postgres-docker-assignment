package com.example.sepm_assignment.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Entity Tests for User
 * Tests getters, setters, equals, hashCode, and behavior
 */
@DisplayName("User Entity Tests")
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    @DisplayName("Should create user with no-args constructor")
    void noArgsConstructor() {
        // Act
        User newUser = new User();

        // Assert
        assertNotNull(newUser);
        assertNull(newUser.getId());
        assertNull(newUser.getUsername());
    }

    @Test
    @DisplayName("Should create user with all-args constructor")
    void allArgsConstructor() {
        // Act
        User newUser = new User(1L, "testuser", "password", "test@example.com",
                "Test User", User.Role.STUDENT, true, null, null);

        // Assert
        assertNotNull(newUser);
        assertEquals(1L, newUser.getId());
        assertEquals("testuser", newUser.getUsername());
        assertEquals("password", newUser.getPassword());
        assertEquals("test@example.com", newUser.getEmail());
        assertEquals("Test User", newUser.getFullName());
        assertEquals(User.Role.STUDENT, newUser.getRole());
        assertTrue(newUser.isEnabled());
    }

    @Test
    @DisplayName("Should set and get ID")
    void setAndGetId() {
        // Act
        user.setId(1L);

        // Assert
        assertEquals(1L, user.getId());
    }

    @Test
    @DisplayName("Should set and get username")
    void setAndGetUsername() {
        // Act
        user.setUsername("testuser");

        // Assert
        assertEquals("testuser", user.getUsername());
    }

    @Test
    @DisplayName("Should set and get password")
    void setAndGetPassword() {
        // Act
        user.setPassword("password123");

        // Assert
        assertEquals("password123", user.getPassword());
    }

    @Test
    @DisplayName("Should set and get email")
    void setAndGetEmail() {
        // Act
        user.setEmail("test@example.com");

        // Assert
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    @DisplayName("Should set and get full name")
    void setAndGetFullName() {
        // Act
        user.setFullName("Test User");

        // Assert
        assertEquals("Test User", user.getFullName());
    }

    @Test
    @DisplayName("Should set and get role")
    void setAndGetRole() {
        // Act
        user.setRole(User.Role.TEACHER);

        // Assert
        assertEquals(User.Role.TEACHER, user.getRole());
    }

    @Test
    @DisplayName("Should set and get enabled status")
    void setAndGetEnabled() {
        // Act
        user.setEnabled(true);

        // Assert
        assertTrue(user.isEnabled());

        // Act
        user.setEnabled(false);

        // Assert
        assertFalse(user.isEnabled());
    }

    @Test
    @DisplayName("Should test equals with same object")
    void equals_SameObject() {
        // Act & Assert
        assertEquals(user, user);
    }

    @Test
    @DisplayName("Should test equals with equal objects")
    void equals_EqualObjects() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("testuser");

        User user2 = new User();
        user2.setId(1L);
        user2.setUsername("testuser");

        // Act & Assert
        assertEquals(user1, user2);
    }

    @Test
    @DisplayName("Should test equals with different objects")
    void equals_DifferentObjects() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("testuser1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("testuser2");

        // Act & Assert
        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("Should test equals with null")
    void equals_Null() {
        // Act & Assert
        assertNotEquals(null, user);
    }

    @Test
    @DisplayName("Should test hashCode consistency")
    void hashCode_Consistency() {
        // Arrange
        user.setId(1L);
        user.setUsername("testuser");

        // Act
        int hashCode1 = user.hashCode();
        int hashCode2 = user.hashCode();

        // Assert
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    @DisplayName("Should test hashCode equality")
    void hashCode_Equality() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("testuser");

        User user2 = new User();
        user2.setId(1L);
        user2.setUsername("testuser");

        // Act & Assert
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    @DisplayName("Should test toString")
    void testToString() {
        // Arrange
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        // Act
        String result = user.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("testuser"));
    }

    @Test
    @DisplayName("Should test all role types")
    void testRoleTypes() {
        // Act & Assert
        assertEquals("STUDENT", User.Role.STUDENT.name());
        assertEquals("TEACHER", User.Role.TEACHER.name());
        assertEquals("ADMIN", User.Role.ADMIN.name());
    }

    @Test
    @DisplayName("Should handle enrollments collection")
    void testEnrollmentsCollection() {
        // Arrange
        user.setEnrollments(new java.util.HashSet<>());

        // Act
        Enrollment enrollment = new Enrollment();
        user.getEnrollments().add(enrollment);

        // Assert
        assertEquals(1, user.getEnrollments().size());
        assertTrue(user.getEnrollments().contains(enrollment));
    }

    @Test
    @DisplayName("Should handle taught courses collection")
    void testTaughtCoursesCollection() {
        // Arrange
        user.setTaughtCourses(new java.util.HashSet<>());

        // Act
        Course course = new Course();
        user.getTaughtCourses().add(course);

        // Assert
        assertEquals(1, user.getTaughtCourses().size());
        assertTrue(user.getTaughtCourses().contains(course));
    }

    @Test
    @DisplayName("Should handle default enabled value")
    void testDefaultEnabledValue() {
        // Arrange
        User newUser = new User();

        // Assert - default is true based on field initialization
        assertTrue(newUser.isEnabled());
    }
}
