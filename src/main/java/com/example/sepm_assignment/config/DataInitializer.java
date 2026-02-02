package com.example.sepm_assignment.config;

import com.example.sepm_assignment.model.User;
import com.example.sepm_assignment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (userRepository.count() == 0) {
                log.info("Initializing default users...");

                // Create Admin user
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@example.com");
                admin.setFullName("System Administrator");
                admin.setRole(User.Role.ADMIN);
                admin.setEnabled(true);
                userRepository.save(admin);
                log.info("Created admin user: admin / admin123");

                // Create Teacher user
                User teacher = new User();
                teacher.setUsername("teacher");
                teacher.setPassword(passwordEncoder.encode("teacher123"));
                teacher.setEmail("teacher@example.com");
                teacher.setFullName("Dr. John Smith");
                teacher.setRole(User.Role.TEACHER);
                teacher.setEnabled(true);
                userRepository.save(teacher);
                log.info("Created teacher user: teacher / teacher123");

                // Create Student user
                User student = new User();
                student.setUsername("student");
                student.setPassword(passwordEncoder.encode("student123"));
                student.setEmail("student@example.com");
                student.setFullName("Jane Doe");
                student.setRole(User.Role.STUDENT);
                student.setEnabled(true);
                userRepository.save(student);
                log.info("Created student user: student / student123");

                log.info("Data initialization completed!");
            } else {
                log.info("Database already contains data. Skipping initialization.");
            }
        };
    }
}
