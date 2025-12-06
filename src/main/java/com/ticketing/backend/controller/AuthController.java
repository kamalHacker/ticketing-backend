package com.ticketing.backend.controller;

import com.ticketing.backend.dto.AuthResponse;
import com.ticketing.backend.dto.LoginRequest;
import com.ticketing.backend.dto.RegisterRequest;
import com.ticketing.backend.service.AuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ticketing.backend.model.User;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/admin/create-agent")
    @PreAuthorize("hasRole('ADMIN')")
    public User createAgent(@RequestBody RegisterRequest request) {
        return authService.createAgent(request);
    }

}
