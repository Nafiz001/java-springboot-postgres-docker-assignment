package com.example.sepm_assignment.controller;

import com.example.sepm_assignment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public String enrollInCourse(@RequestParam Long studentId,
                                  @RequestParam Long courseId,
                                  RedirectAttributes redirectAttributes) {
        try {
            enrollmentService.enrollStudent(studentId, courseId);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully enrolled in the course!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to enroll: " + e.getMessage());
        }
        return "redirect:/student/dashboard";
    }

    @PostMapping("/drop")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public String dropCourse(@RequestParam Long enrollmentId,
                             RedirectAttributes redirectAttributes) {
        try {
            enrollmentService.deleteEnrollment(enrollmentId);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully dropped the course!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to drop course: " + e.getMessage());
        }
        return "redirect:/student/dashboard";
    }
}
