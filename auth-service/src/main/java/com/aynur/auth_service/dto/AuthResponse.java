package com.aynur.auth_service.dto;

import lombok.Builder;
import lombok.Getter;
@Getter
@Builder
public class AuthResponse {
    private Long userId;
    private String accessToken;
    private String refreshToken;
}