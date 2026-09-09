package com.sentinel.service;

import com.sentinel.dto.LoginRequest;
import com.sentinel.dto.LoginResponse;
import com.sentinel.model.User;
import com.sentinel.repository.UserRepository;
import com.sentinel.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(
            user.getUsername(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(
            user.getUsername());

        return new LoginResponse(
            token,
            refreshToken,
            user.getUsername(),
            user.getFullName(),
            user.getRole().name(),
            "Login successful"
        );
    }
}