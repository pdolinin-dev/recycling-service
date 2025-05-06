package com.example.recycling_service.controller;

import com.example.recycling_service.dto.AuthRequest;
import com.example.recycling_service.dto.JwtResponse;
import com.example.recycling_service.dto.LoginRequest;
import com.example.recycling_service.dto.RegisterRequest;
import com.example.recycling_service.model.Token;
import com.example.recycling_service.model.User;
import com.example.recycling_service.service.AuthService;
import com.example.recycling_service.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.recycling_service.security.JwtTokenProvider;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        // 1. Аутентификация пользователя
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // 2. Установка аутентификации в SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Генерация JWT-токена (передаем только имя пользователя)
        String jwt = jwtTokenProvider.generateToken(authentication.getName());

        // 4. Получение ролей пользователя
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // 5. Возврат ответа с токеном
        return ResponseEntity.ok(new JwtResponse(
                jwt,
                authentication.getName(),
                roles.isEmpty() ? "ROLE_USER" : roles.get(0)
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest registerRequest) {
        User registeredUser = authService.registerUser(registerRequest);
        return ResponseEntity.ok(registeredUser);
    }
}