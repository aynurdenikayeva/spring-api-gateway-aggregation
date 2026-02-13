package com.aynur.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class RegisterRequest {
    @NotBlank
    private String username;
    @Email @NotBlank
    private String email;
    @NotBlank
    private String password;
}