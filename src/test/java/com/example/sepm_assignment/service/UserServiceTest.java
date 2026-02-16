package com.example.sepm_assignment.service;

import com.example.sepm_assignment.dto.RegistrationRequest;
import com.example.sepm_assignment.dto.UserDTO;
import com.example.sepm_assignment.model.User;
import com.example.sepm_assignment.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for UserService
 * Tests business logic with mocked dependencies
 * Follows AAA (Arrange-Act-Assert) pattern
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegistrationRequest registrationRequest;

    @BeforeEach
    void setUp() {
        // Arrange: Setup test data
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setRole(User.Role.STUDENT);
        testUser.setEnabled(true);

        registrationRequest = new RegistrationRequest();
        registrationRequest.setUsername("newuser");
        registrationRequest.setPassword("password123");
        registrationRequest.setEmail("newuser@example.com");
        registrationRequest.setFullName("New User");
        registrationRequest.setRole(User.Role.STUDENT);
    }

    @Test
    @DisplayName("Should register user successfully")
    void registerUser_Success() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserDTO result = userService.registerUser(registrationRequest);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).existsByEmail("newuser@example.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void registerUser_UsernameExists_ThrowsException() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(registrationRequest);
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void registerUser_EmailExists_ThrowsException() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(registrationRequest);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail("newuser@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void getUserById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        UserDTO result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user not found by ID")
    void getUserById_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(999L);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get user by username successfully")
    void getUserByUsername_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDTO result = userService.getUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should get all users successfully")
    void getAllUsers_Success() {
        // Arrange
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setFullName("User Two");
        user2.setRole(User.Role.TEACHER);
        user2.setEnabled(true);

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

        // Act
        List<UserDTO> results = userService.getAllUsers();

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get users by role successfully")
    void getUsersByRole_Success() {
        // Arrange
        when(userRepository.findByRole(User.Role.STUDENT)).thenReturn(Arrays.asList(testUser));

        // Act
        List<UserDTO> results = userService.getUsersByRole(User.Role.STUDENT);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(User.Role.STUDENT, results.get(0).getRole());
        verify(userRepository, times(1)).findByRole(User.Role.STUDENT);
    }

    @Test
    @DisplayName("Should delete user successfully")
    void deleteUser_Success() {
        // Arrange
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should toggle user status successfully")
    void toggleUserStatus_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        boolean initialStatus = testUser.isEnabled();

        // Act
        userService.toggleUserStatus(1L);

        // Assert
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(testUser);
        assertEquals(!initialStatus, testUser.isEnabled());
    }

    @Test
    @DisplayName("Should throw exception when toggling non-existent user")
    void toggleUserStatus_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.toggleUserStatus(999L);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should set default role when role is null in registration")
    void registerUser_NullRole_SetsDefaultStudent() {
        // Arrange
        registrationRequest.setRole(null);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // Act
        UserDTO result = userService.registerUser(registrationRequest);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(argThat(user ->
            user.getRole() == User.Role.STUDENT
        ));
    }
}
