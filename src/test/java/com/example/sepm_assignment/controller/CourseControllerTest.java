package com.example.sepm_assignment.controller;

import com.example.sepm_assignment.model.Course;
import com.example.sepm_assignment.model.User;
import com.example.sepm_assignment.repository.CourseRepository;
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
 * Integration Tests for CourseController
 * Tests HTTP requests/responses with full Spring context
 * Uses H2 in-memory database
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("CourseController Integration Tests")
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testTeacher;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        // Clean database
        courseRepository.deleteAll();
        userRepository.deleteAll();

        // Create test teacher
        testTeacher = new User();
        testTeacher.setUsername("teacher");
        testTeacher.setPassword(passwordEncoder.encode("password"));
        testTeacher.setEmail("teacher@example.com");
        testTeacher.setFullName("Test Teacher");
        testTeacher.setRole(User.Role.TEACHER);
        testTeacher.setEnabled(true);
        userRepository.save(testTeacher);

        // Create test course
        testCourse = new Course();
        testCourse.setCourseCode("CS101");
        testCourse.setCourseName("Introduction to Computer Science");
        testCourse.setDescription("Basic CS concepts");
        testCourse.setCredits(3);
        testCourse.setTeacher(testTeacher);
        courseRepository.save(testCourse);
    }

    @Test
    @DisplayName("Should list all courses without authentication")
    void listCourses() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(view().name("courses"))
                .andExpect(model().attributeExists("courses"));
    }

    @Test
    @DisplayName("Should view course details")
    void viewCourse() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/courses/" + testCourse.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("course-detail"))
                .andExpect(model().attributeExists("course"))
                .andExpect(model().attributeExists("enrollments"));
    }

    @Test
    @DisplayName("Should access create course form as teacher")
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void showCreateForm_AsTeacher() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/courses/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("course-form"))
                .andExpect(model().attributeExists("course"))
                .andExpect(model().attributeExists("teachers"));
    }

    @Test
    @DisplayName("Should deny access to create course form for students")
    @WithMockUser(username = "student", roles = "STUDENT")
    void showCreateForm_AsStudent_AccessDenied() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/courses/create"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should create course successfully as teacher")
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void createCourse() throws Exception {
        // Arrange
        String courseCode = "CS102";

        // Act & Assert
        mockMvc.perform(post("/courses/create")
                        .with(csrf())
                        .param("courseCode", courseCode)
                        .param("courseName", "Data Structures")
                        .param("description", "Learn data structures")
                        .param("credits", "4")
                        .param("teacherId", testTeacher.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses"))
                .andExpect(flash().attributeExists("success"))
                .andExpect(flash().attribute("success",
                    containsString("Course created successfully")));

        // Verify course was created
        assert(courseRepository.existsByCourseCode(courseCode));
    }

    @Test
    @DisplayName("Should fail to create course with duplicate course code")
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void createCourse_DuplicateCourseCode_Fails() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/courses/create")
                        .with(csrf())
                        .param("courseCode", "CS101") // Already exists
                        .param("courseName", "Another Course")
                        .param("description", "Description")
                        .param("credits", "3")
                        .param("teacherId", testTeacher.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses/create"))
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error",
                    containsString("Course code already exists")));
    }

    @Test
    @DisplayName("Should access edit course form as teacher")
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void showEditForm() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/courses/" + testCourse.getId() + "/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("course-form"))
                .andExpect(model().attributeExists("course"))
                .andExpect(model().attributeExists("teachers"));
    }

    @Test
    @DisplayName("Should update course successfully as teacher")
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void updateCourse() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/courses/" + testCourse.getId() + "/edit")
                        .with(csrf())
                        .param("courseCode", "CS101")
                        .param("courseName", "Updated Course Name")
                        .param("description", "Updated description")
                        .param("credits", "4")
                        .param("teacherId", testTeacher.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses/" + testCourse.getId()))
                .andExpect(flash().attributeExists("success"))
                .andExpect(flash().attribute("success",
                    containsString("Course updated successfully")));

        // Verify course was updated
        Course updatedCourse = courseRepository.findById(testCourse.getId()).orElseThrow();
        assert(updatedCourse.getCourseName().equals("Updated Course Name"));
    }

    @Test
    @DisplayName("Should delete course successfully as teacher")
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void deleteCourse_AsTeacher() throws Exception {
        // Arrange
        Long courseId = testCourse.getId();

        // Act & Assert
        mockMvc.perform(post("/courses/" + courseId + "/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher/dashboard"))
                .andExpect(flash().attributeExists("successMessage"))
                .andExpect(flash().attribute("successMessage",
                    containsString("Course deleted successfully")));

        // Verify course was deleted
        assert(!courseRepository.existsById(courseId));
    }

    @Test
    @DisplayName("Should delete course and redirect to admin dashboard as admin")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteCourse_AsAdmin() throws Exception {
        // Arrange
        Long courseId = testCourse.getId();

        // Act & Assert
        mockMvc.perform(post("/courses/" + courseId + "/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify course was deleted
        assert(!courseRepository.existsById(courseId));
    }

    @Test
    @DisplayName("Should deny course deletion for students")
    @WithMockUser(username = "student", roles = "STUDENT")
    void deleteCourse_AsStudent_AccessDenied() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/courses/" + testCourse.getId() + "/delete")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should require CSRF token for POST requests")
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void createCourse_RequiresCsrf() throws Exception {
        // Act & Assert - without CSRF token
        mockMvc.perform(post("/courses/create")
                        .param("courseCode", "CS103")
                        .param("courseName", "Algorithms")
                        .param("description", "Algorithm design")
                        .param("credits", "3"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should handle non-existent course gracefully")
    void viewCourse_NotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/courses/999"))
                .andExpect(status().is5xxServerError());
    }
}
