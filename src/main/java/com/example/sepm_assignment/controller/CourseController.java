package com.example.sepm_assignment.controller;

import com.example.sepm_assignment.dto.CourseDTO;
import com.example.sepm_assignment.model.User;
import com.example.sepm_assignment.service.CourseService;
import com.example.sepm_assignment.service.EnrollmentService;
import com.example.sepm_assignment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;
    private final EnrollmentService enrollmentService;

    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "courses";
    }

    @GetMapping("/{id}")
    public String viewCourse(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.getCourseById(id));
        model.addAttribute("enrollments", enrollmentService.getEnrollmentsByCourse(id));
        return "course-detail";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("course", new CourseDTO());
        model.addAttribute("teachers", userService.getUsersByRole(User.Role.TEACHER));
        return "course-form";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public String createCourse(@ModelAttribute CourseDTO courseDTO, RedirectAttributes redirectAttributes) {
        try {
            courseService.createCourse(courseDTO);
            redirectAttributes.addFlashAttribute("success", "Course created successfully!");
            return "redirect:/courses";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/courses/create";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.getCourseById(id));
        model.addAttribute("teachers", userService.getUsersByRole(User.Role.TEACHER));
        return "course-form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public String updateCourse(@PathVariable Long id, @ModelAttribute CourseDTO courseDTO, RedirectAttributes redirectAttributes) {
        try {
            courseService.updateCourse(id, courseDTO);
            redirectAttributes.addFlashAttribute("success", "Course updated successfully!");
            return "redirect:/courses/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/courses/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public String deleteCourse(@PathVariable Long id,
                               RedirectAttributes redirectAttributes,
                               @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        try {
            courseService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully! All enrollments have been removed.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete course: " + e.getMessage());
        }

        // Redirect based on role
        if (userDetails.getAuthorities().stream()
                .anyMatch(a -> a != null && "ROLE_ADMIN".equals(a.getAuthority()))) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/teacher/dashboard";
        }
    }
}
