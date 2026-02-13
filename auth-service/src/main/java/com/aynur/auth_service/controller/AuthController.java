package com.aynur.auth_service.controller;

import com.aynur.auth_service.dto.AuthResponse;
import com.aynur.auth_service.dto.LoginRequest;
import com.aynur.auth_service.dto.RefreshRequest;
import com.aynur.auth_service.dto.RegisterRequest;
import com.aynur.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody @Valid RegisterRequest req) {
        return authService.register(req);
    }
    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest req) {
        System.out.println(">>> LOGIN HIT: " + req);

        return authService.login(req);
    }
    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody @Valid RefreshRequest req) {
        return authService.refresh(req);
    }
    @PostMapping("/logout")
    public void logout(@RequestBody @Valid RefreshRequest req) {
        authService.logout(req);
    }
}

