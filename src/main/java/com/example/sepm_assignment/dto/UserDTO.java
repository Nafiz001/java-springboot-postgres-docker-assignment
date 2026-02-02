package com.example.sepm_assignment.dto;

import com.example.sepm_assignment.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private User.Role role;
    private boolean enabled;
}
