package com.example.sepm_assignment.controller.api;

import com.example.sepm_assignment.dto.EnrollmentDTO;
import com.example.sepm_assignment.model.Enrollment;
import com.example.sepm_assignment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentRestController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<EnrollmentDTO> enrollStudent(@RequestBody Map<String, Long> request) {
        Long studentId = request.get("studentId");
        Long courseId = request.get("courseId");
        EnrollmentDTO enrollment = enrollmentService.enrollStudent(studentId, courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollment);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(studentId));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(courseId));
    }

    @PutMapping("/{id}/grade")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<EnrollmentDTO> updateGrade(@PathVariable Long id, @RequestBody Map<String, Double> request) {
        Double grade = request.get("grade");
        EnrollmentDTO updated = enrollmentService.updateGrade(id, grade);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<EnrollmentDTO> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        Enrollment.EnrollmentStatus status = Enrollment.EnrollmentStatus.valueOf(request.get("status"));
        EnrollmentDTO updated = enrollmentService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
        return ResponseEntity.noContent().build();
    }
}
