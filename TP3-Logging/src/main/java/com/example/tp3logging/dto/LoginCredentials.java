package com.example.tp3logging.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "password")
public class LoginCredentials {
    @Email(message = "Email should be valid")
    private String email;
    private String password;
}
