package com.example.sepm_assignment.service;

import com.example.sepm_assignment.dto.EnrollmentDTO;
import com.example.sepm_assignment.model.Course;
import com.example.sepm_assignment.model.Enrollment;
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
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public EnrollmentDTO enrollStudent(Long studentId, Long courseId) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new RuntimeException("Student already enrolled in this course");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        if (student.getRole() != User.Role.STUDENT) {
            throw new RuntimeException("User is not a student");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStatus(Enrollment.EnrollmentStatus.ACTIVE);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return convertToDTO(savedEnrollment);
    }

    public List<EnrollmentDTO> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EnrollmentDTO> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public EnrollmentDTO updateGrade(Long enrollmentId, Double grade) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        enrollment.setGrade(grade);
        Enrollment updated = enrollmentRepository.save(enrollment);
        return convertToDTO(updated);
    }

    @Transactional
    public EnrollmentDTO updateStatus(Long enrollmentId, Enrollment.EnrollmentStatus status) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        enrollment.setStatus(status);
        Enrollment updated = enrollmentRepository.save(enrollment);
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteEnrollment(Long id) {
        enrollmentRepository.deleteById(id);
    }

    private EnrollmentDTO convertToDTO(Enrollment enrollment) {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(enrollment.getId());
        dto.setStudentId(enrollment.getStudent().getId());
        dto.setStudentName(enrollment.getStudent().getFullName());
        dto.setCourseId(enrollment.getCourse().getId());
        dto.setCourseName(enrollment.getCourse().getCourseName());
        dto.setCourseCode(enrollment.getCourse().getCourseCode());
        dto.setEnrollmentDate(enrollment.getEnrollmentDate());
        dto.setStatus(enrollment.getStatus());
        dto.setGrade(enrollment.getGrade());
        return dto;
    }
}
