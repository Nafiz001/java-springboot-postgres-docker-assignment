package com.example.sepm_assignment.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Entity Tests for Course
 * Tests getters, setters, equals, hashCode, and behavior
 */
@DisplayName("Course Entity Tests")
class CourseTest {

    private Course course;

    @BeforeEach
    void setUp() {
        course = new Course();
    }

    @Test
    @DisplayName("Should create course with no-args constructor")
    void noArgsConstructor() {
        // Act
        Course newCourse = new Course();

        // Assert
        assertNotNull(newCourse);
        assertNull(newCourse.getId());
        assertNull(newCourse.getCourseCode());
    }

    @Test
    @DisplayName("Should create course with all-args constructor")
    void allArgsConstructor() {
        // Arrange
        User teacher = new User();
        teacher.setId(1L);

        // Act
        Course newCourse = new Course(1L, "CS101", "Introduction to CS",
                "Basic concepts", 3, teacher, null);

        // Assert
        assertNotNull(newCourse);
        assertEquals(1L, newCourse.getId());
        assertEquals("CS101", newCourse.getCourseCode());
        assertEquals("Introduction to CS", newCourse.getCourseName());
        assertEquals("Basic concepts", newCourse.getDescription());
        assertEquals(3, newCourse.getCredits());
        assertEquals(teacher, newCourse.getTeacher());
    }

    @Test
    @DisplayName("Should set and get ID")
    void setAndGetId() {
        // Act
        course.setId(1L);

        // Assert
        assertEquals(1L, course.getId());
    }

    @Test
    @DisplayName("Should set and get course code")
    void setAndGetCourseCode() {
        // Act
        course.setCourseCode("CS101");

        // Assert
        assertEquals("CS101", course.getCourseCode());
    }

    @Test
    @DisplayName("Should set and get course name")
    void setAndGetCourseName() {
        // Act
        course.setCourseName("Data Structures");

        // Assert
        assertEquals("Data Structures", course.getCourseName());
    }

    @Test
    @DisplayName("Should set and get description")
    void setAndGetDescription() {
        // Act
        course.setDescription("Learn about data structures");

        // Assert
        assertEquals("Learn about data structures", course.getDescription());
    }

    @Test
    @DisplayName("Should set and get credits")
    void setAndGetCredits() {
        // Act
        course.setCredits(4);

        // Assert
        assertEquals(4, course.getCredits());
    }

    @Test
    @DisplayName("Should set and get teacher")
    void setAndGetTeacher() {
        // Arrange
        User teacher = new User();
        teacher.setId(1L);
        teacher.setUsername("teacher1");

        // Act
        course.setTeacher(teacher);

        // Assert
        assertEquals(teacher, course.getTeacher());
        assertEquals(1L, course.getTeacher().getId());
    }

    @Test
    @DisplayName("Should test equals with same object")
    void equals_SameObject() {
        // Act & Assert
        assertEquals(course, course);
    }

    @Test
    @DisplayName("Should test equals with equal objects")
    void equals_EqualObjects() {
        // Arrange
        Course course1 = new Course();
        course1.setId(1L);
        course1.setCourseCode("CS101");

        Course course2 = new Course();
        course2.setId(1L);
        course2.setCourseCode("CS101");

        // Act & Assert
        assertEquals(course1, course2);
    }

    @Test
    @DisplayName("Should test equals with different objects")
    void equals_DifferentObjects() {
        // Arrange
        Course course1 = new Course();
        course1.setId(1L);
        course1.setCourseCode("CS101");

        Course course2 = new Course();
        course2.setId(2L);
        course2.setCourseCode("CS102");

        // Act & Assert
        assertNotEquals(course1, course2);
    }

    @Test
    @DisplayName("Should test equals with null")
    void equals_Null() {
        // Act & Assert
        assertNotEquals(null, course);
    }

    @Test
    @DisplayName("Should test hashCode consistency")
    void hashCode_Consistency() {
        // Arrange
        course.setId(1L);
        course.setCourseCode("CS101");

        // Act
        int hashCode1 = course.hashCode();
        int hashCode2 = course.hashCode();

        // Assert
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    @DisplayName("Should test hashCode equality")
    void hashCode_Equality() {
        // Arrange
        Course course1 = new Course();
        course1.setId(1L);
        course1.setCourseCode("CS101");

        Course course2 = new Course();
        course2.setId(1L);
        course2.setCourseCode("CS101");

        // Act & Assert
        assertEquals(course1.hashCode(), course2.hashCode());
    }

    @Test
    @DisplayName("Should test toString")
    void testToString() {
        // Arrange
        course.setId(1L);
        course.setCourseCode("CS101");
        course.setCourseName("Introduction to CS");

        // Act
        String result = course.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("CS101"));
    }

    @Test
    @DisplayName("Should handle enrollments collection")
    void testEnrollmentsCollection() {
        // Arrange
        course.setEnrollments(new java.util.HashSet<>());

        // Act
        Enrollment enrollment = new Enrollment();
        course.getEnrollments().add(enrollment);

        // Assert
        assertEquals(1, course.getEnrollments().size());
        assertTrue(course.getEnrollments().contains(enrollment));
    }

    @Test
    @DisplayName("Should handle null teacher")
    void testNullTeacher() {
        // Act
        course.setTeacher(null);

        // Assert
        assertNull(course.getTeacher());
    }

    @Test
    @DisplayName("Should handle relationship with teacher")
    void testTeacherRelationship() {
        // Arrange
        User teacher = new User();
        teacher.setId(1L);
        teacher.setUsername("teacher1");
        teacher.setRole(User.Role.TEACHER);

        // Act
        course.setTeacher(teacher);

        // Assert
        assertNotNull(course.getTeacher());
        assertEquals(User.Role.TEACHER, course.getTeacher().getRole());
    }

    @Test
    @DisplayName("Should handle large description")
    void testLargeDescription() {
        // Arrange
        String largeDescription = "A".repeat(1000);

        // Act
        course.setDescription(largeDescription);

        // Assert
        assertEquals(1000, course.getDescription().length());
    }

    @Test
    @DisplayName("Should handle various credit values")
    void testCreditValues() {
        // Test minimum credits
        course.setCredits(1);
        assertEquals(1, course.getCredits());

        // Test maximum typical credits
        course.setCredits(6);
        assertEquals(6, course.getCredits());
    }
}
