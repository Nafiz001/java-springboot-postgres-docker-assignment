package com.example.sepm_assignment.controller;

import com.example.sepm_assignment.model.User;
import com.example.sepm_assignment.service.CourseService;
import com.example.sepm_assignment.service.EnrollmentService;
import com.example.sepm_assignment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        var user = userService.getUserByUsername(username);

        model.addAttribute("user", user);
        model.addAttribute("courses", courseService.getAllCourses());

        // Role-based redirects
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
            model.addAttribute("enrollments", enrollmentService.getEnrollmentsByStudent(user.getId()));
            return "student-dashboard";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TEACHER"))) {
            model.addAttribute("myCourses", courseService.getCoursesByTeacher(user.getId()));
            return "teacher-dashboard";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            model.addAttribute("users", userService.getAllUsers());
            return "admin-dashboard";
        }

        return "dashboard";
    }

    @GetMapping("/student/dashboard")
    @PreAuthorize("hasRole('STUDENT')")
    public String studentDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        var user = userService.getUserByUsername(username);

        model.addAttribute("user", user);
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("enrollments", enrollmentService.getEnrollmentsByStudent(user.getId()));

        return "student-dashboard";
    }

    @GetMapping("/teacher/dashboard")
    @PreAuthorize("hasRole('TEACHER')")
    public String teacherDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        var user = userService.getUserByUsername(username);

        model.addAttribute("user", user);
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("myCourses", courseService.getCoursesByTeacher(user.getId()));

        return "teacher-dashboard";
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        var user = userService.getUserByUsername(username);

        model.addAttribute("user", user);
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("users", userService.getAllUsers());

        return "admin-dashboard";
    }
}
