package com.example.sepm_assignment.dto;

import com.example.sepm_assignment.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
    private String username;
    private String password;
    private String email;
    private String fullName;
    private User.Role role;
}
