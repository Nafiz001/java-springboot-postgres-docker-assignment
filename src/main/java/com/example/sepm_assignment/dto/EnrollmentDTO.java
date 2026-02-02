package com.example.sepm_assignment.dto;

import com.example.sepm_assignment.model.Enrollment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseName;
    private String courseCode;
    private LocalDateTime enrollmentDate;
    private Enrollment.EnrollmentStatus status;
    private Double grade;
}
