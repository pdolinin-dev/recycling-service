package com.example.recycling_service.controller;

import com.example.recycling_service.dto.AuthRequest;
import com.example.recycling_service.model.Token;
import com.example.recycling_service.service.TokenService;
import com.example.recycling_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<Token> login(@RequestBody AuthRequest authRequest) {
        // Аутентификация пользователя
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        // Генерация токена
        String token = tokenService.generateToken(authRequest.getUsername());

        // Возвращаем токен
        return ResponseEntity.ok(new Token(token));
    }
}