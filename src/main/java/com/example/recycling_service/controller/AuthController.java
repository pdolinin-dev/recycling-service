package com.example.recycling_service.controller;

import com.example.recycling_service.dto.JwtResponse;
import com.example.recycling_service.dto.LoginRequest;
import com.example.recycling_service.dto.RegisterRequest;
import com.example.recycling_service.model.User;
import com.example.recycling_service.security.JwtTokenProvider;
import com.example.recycling_service.service.AuthService;
import com.example.recycling_service.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.AuthenticationServiceException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

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
                roles.isEmpty() ? "ROLE_PHYS" : roles.get(0)
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            if (authService.existsByUsername(registerRequest.getUsername())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of(
                                "status", "error",
                                "message", "Пользователь с таким логином уже существует",
                                "field", "username"
                        ));
            }

            if (authService.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of(
                                "status", "error",
                                "message", "Пользователь с таким email уже существует",
                                "field", "email"
                        ));
            }

            // Регистрируем пользователя
            User registeredUser = authService.registerUser(registerRequest);

            // Аутентифицируем с оригинальным паролем из запроса
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            registerRequest.getUsername(),
                            registerRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Генерация токена
            String jwt = jwtTokenProvider.generateToken(authentication.getName());

            // Получение ролей (гарантируем хотя бы ROLE_USER)
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            if (roles.isEmpty()) {
                roles = List.of("ROLE_USER");
            }

            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    authentication.getName(),
                    roles.get(0)
            ));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ошибка аутентификации");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка сервера");
        }
    }
}