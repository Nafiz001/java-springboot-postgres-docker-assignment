package com.example.sepm_assignment.repository;

import com.example.sepm_assignment.model.Course;
import com.example.sepm_assignment.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository Tests for CourseRepository
 * Tests data access layer with H2 in-memory database
 * Uses @DataJpaTest for lightweight testing
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CourseRepository Tests")
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testTeacher;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        // Clean database
        courseRepository.deleteAll();
        userRepository.deleteAll();

        // Create test teacher
        testTeacher = new User();
        testTeacher.setUsername("teacher1");
        testTeacher.setPassword("password");
        testTeacher.setEmail("teacher1@example.com");
        testTeacher.setFullName("Test Teacher");
        testTeacher.setRole(User.Role.TEACHER);
        testTeacher.setEnabled(true);
        testTeacher = userRepository.save(testTeacher);

        // Create test course
        testCourse = new Course();
        testCourse.setCourseCode("CS101");
        testCourse.setCourseName("Introduction to Computer Science");
        testCourse.setDescription("Basic CS concepts");
        testCourse.setCredits(3);
        testCourse.setTeacher(testTeacher);
        testCourse = courseRepository.save(testCourse);
    }

    @Test
    @DisplayName("Should save course successfully")
    void save() {
        // Arrange
        Course newCourse = new Course();
        newCourse.setCourseCode("CS102");
        newCourse.setCourseName("Data Structures");
        newCourse.setDescription("Learn data structures");
        newCourse.setCredits(4);
        newCourse.setTeacher(testTeacher);

        // Act
        Course savedCourse = courseRepository.save(newCourse);

        // Assert
        assertNotNull(savedCourse.getId());
        assertEquals("CS102", savedCourse.getCourseCode());
    }

    @Test
    @DisplayName("Should find course by ID successfully")
    void findById() {
        // Act
        Optional<Course> found = courseRepository.findById(testCourse.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("CS101", found.get().getCourseCode());
    }

    @Test
    @DisplayName("Should return empty when course not found by ID")
    void findById_NotFound() {
        // Act
        Optional<Course> found = courseRepository.findById(999L);

        // Assert
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should find course by course code successfully")
    void findByCourseCode() {
        // Act
        Optional<Course> found = courseRepository.findByCourseCode("CS101");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Introduction to Computer Science", found.get().getCourseName());
    }

    @Test
    @DisplayName("Should return empty when course code not found")
    void findByCourseCode_NotFound() {
        // Act
        Optional<Course> found = courseRepository.findByCourseCode("NONEXISTENT");

        // Assert
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should check if course code exists")
    void existsByCourseCode() {
        // Act & Assert
        assertTrue(courseRepository.existsByCourseCode("CS101"));
        assertFalse(courseRepository.existsByCourseCode("NONEXISTENT"));
    }

    @Test
    @DisplayName("Should find courses by teacher ID")
    void findByTeacherId() {
        // Arrange
        Course course2 = new Course();
        course2.setCourseCode("CS201");
        course2.setCourseName("Algorithms");
        course2.setDescription("Algorithm design");
        course2.setCredits(3);
        course2.setTeacher(testTeacher);
        courseRepository.save(course2);

        // Act
        List<Course> courses = courseRepository.findByTeacherId(testTeacher.getId());

        // Assert
        assertEquals(2, courses.size());
    }

    @Test
    @DisplayName("Should return empty list when teacher has no courses")
    void findByTeacherId_NoCourses() {
        // Arrange
        User anotherTeacher = new User();
        anotherTeacher.setUsername("teacher2");
        anotherTeacher.setPassword("password");
        anotherTeacher.setEmail("teacher2@example.com");
        anotherTeacher.setFullName("Another Teacher");
        anotherTeacher.setRole(User.Role.TEACHER);
        anotherTeacher.setEnabled(true);
        anotherTeacher = userRepository.save(anotherTeacher);

        // Act
        List<Course> courses = courseRepository.findByTeacherId(anotherTeacher.getId());

        // Assert
        assertTrue(courses.isEmpty());
    }

    @Test
    @DisplayName("Should delete course by ID")
    void deleteById() {
        // Arrange
        Long courseId = testCourse.getId();

        // Act
        courseRepository.deleteById(courseId);

        // Assert
        assertFalse(courseRepository.existsById(courseId));
    }

    @Test
    @DisplayName("Should find all courses")
    void findAll() {
        // Act
        List<Course> courses = courseRepository.findAll();

        // Assert
        assertEquals(1, courses.size());
    }

    @Test
    @DisplayName("Should update course successfully")
    void update() {
        // Arrange
        Course course = courseRepository.findById(testCourse.getId()).orElseThrow();
        course.setCourseName("Updated Course Name");

        // Act
        Course updated = courseRepository.save(course);

        // Assert
        assertEquals("Updated Course Name", updated.getCourseName());
    }

    @Test
    @DisplayName("Should enforce unique course code constraint")
    void save_DuplicateCourseCode_ThrowsException() {
        // Arrange
        Course duplicateCourse = new Course();
        duplicateCourse.setCourseCode("CS101"); // Duplicate
        duplicateCourse.setCourseName("Different Course");
        duplicateCourse.setDescription("Different description");
        duplicateCourse.setCredits(3);
        duplicateCourse.setTeacher(testTeacher);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            courseRepository.save(duplicateCourse);
            courseRepository.flush(); // Force constraint check
        });
    }

    @Test
    @DisplayName("Should save course without teacher")
    void save_WithoutTeacher() {
        // Arrange
        Course courseWithoutTeacher = new Course();
        courseWithoutTeacher.setCourseCode("CS999");
        courseWithoutTeacher.setCourseName("Self-Study Course");
        courseWithoutTeacher.setDescription("No teacher assigned");
        courseWithoutTeacher.setCredits(2);

        // Act
        Course saved = courseRepository.save(courseWithoutTeacher);

        // Assert
        assertNotNull(saved.getId());
        assertNull(saved.getTeacher());
    }

    @Test
    @DisplayName("Should maintain relationship with teacher")
    void relationshipWithTeacher() {
        // Act
        Course course = courseRepository.findById(testCourse.getId()).orElseThrow();

        // Assert
        assertNotNull(course.getTeacher());
        assertEquals(testTeacher.getId(), course.getTeacher().getId());
        assertEquals("teacher1", course.getTeacher().getUsername());
    }

    @Test
    @DisplayName("Should handle transactional behavior correctly")
    void transactionalBehavior() {
        // Arrange
        long initialCount = courseRepository.count();

        // Act
        Course course = new Course();
        course.setCourseCode("CS300");
        course.setCourseName("Advanced Topics");
        course.setDescription("Advanced course");
        course.setCredits(4);
        courseRepository.save(course);

        // Assert
        assertEquals(initialCount + 1, courseRepository.count());
    }
}
