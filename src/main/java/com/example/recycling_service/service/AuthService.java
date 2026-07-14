package com.example.recycling_service.service;

import com.example.recycling_service.dto.Request.LoginRequest;
import com.example.recycling_service.dto.Request.RegisterRequest;
import com.example.recycling_service.dto.Response.JwtResponse;
import com.example.recycling_service.exception.ConflictException;

import com.example.recycling_service.model.Enum.Role;
import com.example.recycling_service.model.User;
import com.example.recycling_service.repository.UserRepository;
import com.example.recycling_service.security.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    private boolean existsByUsername(String login) {
        return userRepository.existsByLogin(login);
    }

    private boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Генерация JWT
    private String generateJwtToken(String login) {
        return jwtTokenProvider.generateToken(login);
    }

    // Аутентификация по логину паролю
    private Authentication authenticate(String login, String password) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        login,
                        password
                )
        );
    }

    // Логин
    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticate(request.getLogin(), request.getPassword());
        String jwt = generateJwtToken(authentication.getName());
        String role = getUserRole(authentication);

        return createJwtResponse(authentication.getName(), role, jwt);
    }

    // Получение роли из аутентификации
    private String getUserRole(Authentication authentication) {
        String role;
        try {
            role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList().getFirst();
        } catch (IndexOutOfBoundsException ex) {
            role = "ROLE_" + Role.USER.name();
        }

        return role;
    }

    // Обработка некорректных кредов
    private void validateRegistration(RegisterRequest request) {
        if (existsByUsername(request.getLogin())) {
            throw new ConflictException("Пользователь", "login");
        }

        if (existsByEmail(request.getEmail())) {
            throw new ConflictException("Пользователь", "email");
        }
    }

    // Создание нового пользователя при регистрации
    private User createUser(RegisterRequest request) {
        User user = new User();
        user.setLogin(request.getLogin());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    // Регистрация
    public JwtResponse register(RegisterRequest registerRequest) {
        validateRegistration(registerRequest);
        User createdUser = createUser(registerRequest);
        String jwt = generateJwtToken(createdUser.getLogin());

        return createJwtResponse(createdUser.getLogin(), createdUser.getRole().name(), jwt);
    }

    // Создание объекта ответа с JWT
    private JwtResponse createJwtResponse(String login, String role, String jwt) {
        return new JwtResponse(
                login,
                role,
                jwt
        );
    }
}
