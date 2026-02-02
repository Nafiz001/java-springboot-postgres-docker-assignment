package com.example.sepm_assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Long id;
    private String courseCode;
    private String courseName;
    private String description;
    private Integer credits;
    private Long teacherId;
    private String teacherName;
}
