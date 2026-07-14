package com.example.recycling_service.controller;

import com.example.recycling_service.dto.Response.JwtResponse;
import com.example.recycling_service.dto.Request.LoginRequest;
import com.example.recycling_service.dto.Request.RegisterRequest;
import com.example.recycling_service.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    /**
     * Login a user into the system
     * @param loginRequest LoginRequest
     * @return JwtResponse
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    /**
     * Register a user into the system
     * @param registerRequest RegisterRequest
     * @return JwtResponse
     */
    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }
}