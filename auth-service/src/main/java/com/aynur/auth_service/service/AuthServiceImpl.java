package com.aynur.auth_service.service;

import com.aynur.auth_service.dto.AuthResponse;
import com.aynur.auth_service.dto.LoginRequest;
import com.aynur.auth_service.dto.RefreshRequest;
import com.aynur.auth_service.dto.RegisterRequest;
import com.aynur.auth_service.entity.RefreshToken;
import com.aynur.auth_service.entity.Role;
import com.aynur.auth_service.entity.User;
import com.aynur.auth_service.exception.ApiException;
import com.aynur.auth_service.repository.RefreshTokenRepository;
import com.aynur.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // refresh token TTL (məs: 7 gün)
    private final long refreshTtlSeconds = 60L * 60 * 24 * 7;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepo.existsByUsername(request.getUsername()))
            throw new ApiException(HttpStatus.CONFLICT, "Username already exists");

        if (userRepo.existsByEmail(request.getEmail()))
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists");

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        user = userRepo.save(user);

        String access = jwtService.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        String refresh = createRefreshToken(user).getToken();

        return AuthResponse.builder()
                .userId(user.getId())
                .accessToken(access)
                .refreshToken(refresh)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepo.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepo.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");

        String access = jwtService.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        String refresh = createRefreshToken(user).getToken();

        return AuthResponse.builder()
                .userId(user.getId())
                .accessToken(access)
                .refreshToken(refresh)
                .build();
    }

    @Override
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken rt = refreshRepo.findByTokenWithUser(request.getRefreshToken())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (rt.isRevoked() || rt.getExpiresAt().isBefore(Instant.now()))
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token expired or revoked");

        User user = rt.getUser();
        String access = jwtService.generateAccessToken(user.getId(), user.getUsername(), user.getRole());

        return AuthResponse.builder()
                .userId(user.getId())
                .accessToken(access)
                .refreshToken(rt.getToken())
                .build();
    }


    @Override
    public void logout(RefreshRequest request) {
        RefreshToken rt = refreshRepo.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Refresh token not found"));
        rt.setRevoked(true);
        refreshRepo.save(rt);
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken rt = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(Instant.now().plusSeconds(refreshTtlSeconds))
                .revoked(false)
                .build();
        return refreshRepo.save(rt);
    }
}
