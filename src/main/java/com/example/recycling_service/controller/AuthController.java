package com.example.recycling_service.controller;

import com.example.recycling_service.dto.AuthRequest;
import com.example.recycling_service.model.Token;
import com.example.recycling_service.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<Token> login(@RequestBody AuthRequest authRequest) {
        // Генерация токена (если токен все еще нужен для других целей)
        String token = tokenService.generateToken(authRequest.getUsername());

        // Возвращаем токен
        return ResponseEntity.ok(new Token(token));
    }
}