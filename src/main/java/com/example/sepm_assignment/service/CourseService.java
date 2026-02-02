package com.example.sepm_assignment.service;

import com.example.sepm_assignment.dto.CourseDTO;
import com.example.sepm_assignment.model.Course;
import com.example.sepm_assignment.model.User;
import com.example.sepm_assignment.repository.CourseRepository;
import com.example.sepm_assignment.repository.EnrollmentRepository;
import com.example.sepm_assignment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Transactional
    public CourseDTO createCourse(CourseDTO courseDTO) {
        if (courseRepository.existsByCourseCode(courseDTO.getCourseCode())) {
            throw new RuntimeException("Course code already exists");
        }

        Course course = new Course();
        course.setCourseCode(courseDTO.getCourseCode());
        course.setCourseName(courseDTO.getCourseName());
        course.setDescription(courseDTO.getDescription());
        course.setCredits(courseDTO.getCredits());

        if (courseDTO.getTeacherId() != null) {
            User teacher = userRepository.findById(courseDTO.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));
            if (teacher.getRole() != User.Role.TEACHER) {
                throw new RuntimeException("User is not a teacher");
            }
            course.setTeacher(teacher);
        }

        Course savedCourse = courseRepository.save(course);
        return convertToDTO(savedCourse);
    }

    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return convertToDTO(course);
    }

    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CourseDTO> getCoursesByTeacher(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setCourseName(courseDTO.getCourseName());
        course.setDescription(courseDTO.getDescription());
        course.setCredits(courseDTO.getCredits());

        if (courseDTO.getTeacherId() != null) {
            User teacher = userRepository.findById(courseDTO.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));
            course.setTeacher(teacher);
        }

        Course updatedCourse = courseRepository.save(course);
        return convertToDTO(updatedCourse);
    }

    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        // Get enrollment count before deletion for logging
        List<com.example.sepm_assignment.model.Enrollment> enrollments = enrollmentRepository.findByCourseId(id);
        int enrollmentCount = enrollments.size();

        // Explicitly delete all enrollments for this course first
        enrollmentRepository.deleteByCourseId(id);

        // Now delete the course (no foreign key constraint violation)
        courseRepository.delete(course);

        // Log for debugging
        System.out.println("Deleted course: " + course.getCourseName() + " (ID: " + id + ") and " + enrollmentCount + " enrollment(s)");
    }

    private CourseDTO convertToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setCourseCode(course.getCourseCode());
        dto.setCourseName(course.getCourseName());
        dto.setDescription(course.getDescription());
        dto.setCredits(course.getCredits());
        if (course.getTeacher() != null) {
            dto.setTeacherId(course.getTeacher().getId());
            dto.setTeacherName(course.getTeacher().getFullName());
        }
        return dto;
    }
}
