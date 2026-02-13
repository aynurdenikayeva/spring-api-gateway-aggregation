package com.aynur.auth_service.service;

import com.aynur.auth_service.dto.AuthResponse;
import com.aynur.auth_service.dto.LoginRequest;
import com.aynur.auth_service.dto.RefreshRequest;
import com.aynur.auth_service.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refresh(RefreshRequest request);
    void logout(RefreshRequest request);
}