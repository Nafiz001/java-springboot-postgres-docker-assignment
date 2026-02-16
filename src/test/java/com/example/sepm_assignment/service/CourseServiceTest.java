package com.example.sepm_assignment.service;

import com.example.sepm_assignment.dto.CourseDTO;
import com.example.sepm_assignment.model.Course;
import com.example.sepm_assignment.model.Enrollment;
import com.example.sepm_assignment.model.User;
import com.example.sepm_assignment.repository.CourseRepository;
import com.example.sepm_assignment.repository.EnrollmentRepository;
import com.example.sepm_assignment.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for CourseService
 * Tests business logic with mocked dependencies
 * Follows AAA (Arrange-Act-Assert) pattern
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CourseService Unit Tests")
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private CourseService courseService;

    private Course testCourse;
    private CourseDTO courseDTO;
    private User testTeacher;

    @BeforeEach
    void setUp() {
        // Arrange: Setup test data
        testTeacher = new User();
        testTeacher.setId(1L);
        testTeacher.setUsername("teacher");
        testTeacher.setRole(User.Role.TEACHER);
        testTeacher.setEmail("teacher@example.com");
        testTeacher.setFullName("Test Teacher");
        testTeacher.setEnabled(true);

        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setCourseCode("CS101");
        testCourse.setCourseName("Introduction to Computer Science");
        testCourse.setDescription("Basic CS concepts");
        testCourse.setCredits(3);
        testCourse.setTeacher(testTeacher);

        courseDTO = new CourseDTO();
        courseDTO.setCourseCode("CS102");
        courseDTO.setCourseName("Data Structures");
        courseDTO.setDescription("Learn data structures");
        courseDTO.setCredits(4);
        courseDTO.setTeacherId(1L);
    }

    @Test
    @DisplayName("Should create course successfully")
    void createCourse_Success() {
        // Arrange
        when(courseRepository.existsByCourseCode(anyString())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testTeacher));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // Act
        CourseDTO result = courseService.createCourse(courseDTO);

        // Assert
        assertNotNull(result);
        assertEquals("CS101", result.getCourseCode());
        verify(courseRepository, times(1)).existsByCourseCode("CS102");
        verify(userRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @DisplayName("Should throw exception when course code already exists")
    void createCourse_CourseCodeExists_ThrowsException() {
        // Arrange
        when(courseRepository.existsByCourseCode(anyString())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            courseService.createCourse(courseDTO);
        });

        assertEquals("Course code already exists", exception.getMessage());
        verify(courseRepository, times(1)).existsByCourseCode("CS102");
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Should throw exception when teacher not found")
    void createCourse_TeacherNotFound_ThrowsException() {
        // Arrange
        when(courseRepository.existsByCourseCode(anyString())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            courseService.createCourse(courseDTO);
        });

        assertEquals("Teacher not found", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Should throw exception when user is not a teacher")
    void createCourse_UserNotTeacher_ThrowsException() {
        // Arrange
        User student = new User();
        student.setId(1L);
        student.setRole(User.Role.STUDENT);

        when(courseRepository.existsByCourseCode(anyString())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            courseService.createCourse(courseDTO);
        });

        assertEquals("User is not a teacher", exception.getMessage());
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Should get course by ID successfully")
    void getCourseById_Success() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        // Act
        CourseDTO result = courseService.getCourseById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("CS101", result.getCourseCode());
        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when course not found by ID")
    void getCourseById_NotFound_ThrowsException() {
        // Arrange
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            courseService.getCourseById(999L);
        });

        assertEquals("Course not found", exception.getMessage());
        verify(courseRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get all courses successfully")
    void getAllCourses_Success() {
        // Arrange
        Course course2 = new Course();
        course2.setId(2L);
        course2.setCourseCode("CS201");
        course2.setCourseName("Algorithms");
        course2.setCredits(3);

        when(courseRepository.findAll()).thenReturn(Arrays.asList(testCourse, course2));

        // Act
        List<CourseDTO> results = courseService.getAllCourses();

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get courses by teacher ID successfully")
    void getCoursesByTeacher_Success() {
        // Arrange
        when(courseRepository.findByTeacherId(1L)).thenReturn(Arrays.asList(testCourse));

        // Act
        List<CourseDTO> results = courseService.getCoursesByTeacher(1L);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("CS101", results.get(0).getCourseCode());
        verify(courseRepository, times(1)).findByTeacherId(1L);
    }

    @Test
    @DisplayName("Should update course successfully")
    void updateCourse_Success() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testTeacher));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // Act
        CourseDTO result = courseService.updateCourse(1L, courseDTO);

        // Assert
        assertNotNull(result);
        verify(courseRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).save(testCourse);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent course")
    void updateCourse_CourseNotFound_ThrowsException() {
        // Arrange
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            courseService.updateCourse(999L, courseDTO);
        });

        assertEquals("Course not found", exception.getMessage());
        verify(courseRepository, times(1)).findById(999L);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Should delete course and enrollments successfully")
    void deleteCourse_Success() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(enrollmentRepository.findByCourseId(1L)).thenReturn(Arrays.asList(new Enrollment()));
        doNothing().when(enrollmentRepository).deleteByCourseId(1L);
        doNothing().when(courseRepository).delete(testCourse);

        // Act
        courseService.deleteCourse(1L);

        // Assert
        verify(courseRepository, times(1)).findById(1L);
        verify(enrollmentRepository, times(1)).findByCourseId(1L);
        verify(enrollmentRepository, times(1)).deleteByCourseId(1L);
        verify(courseRepository, times(1)).delete(testCourse);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent course")
    void deleteCourse_CourseNotFound_ThrowsException() {
        // Arrange
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            courseService.deleteCourse(999L);
        });

        assertTrue(exception.getMessage().contains("Course not found"));
        verify(courseRepository, times(1)).findById(999L);
        verify(courseRepository, never()).delete(any(Course.class));
    }

    @Test
    @DisplayName("Should create course without teacher")
    void createCourse_WithoutTeacher_Success() {
        // Arrange
        courseDTO.setTeacherId(null);
        when(courseRepository.existsByCourseCode(anyString())).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // Act
        CourseDTO result = courseService.createCourse(courseDTO);

        // Assert
        assertNotNull(result);
        verify(courseRepository, times(1)).save(any(Course.class));
        verify(userRepository, never()).findById(any());
    }
}
