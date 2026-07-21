package com.example.recycling_service.controller;

import com.example.recycling_service.dto.Request.UpdateUserRequest;
import com.example.recycling_service.dto.UserProfileDto;
import com.example.recycling_service.model.User;
import com.example.recycling_service.repository.UserRepository;
import com.example.recycling_service.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService; // Оставляем только сервис
    private final UserRepository userRepository;

    @GetMapping("/profile")
    public UserProfileDto getUserProfile(Authentication authentication, HttpServletResponse response) {
        log.info("Запрос профиля пользователя {}", authentication.getName());
        return userService.getUserProfileWithAdvertisements(authentication.getName());
    }

    @Transactional
    @DeleteMapping()
    public ResponseEntity<Void> deleteUserProfile(Authentication authentication) {
        log.warn("Запрос на удаление пользователя");
        userService.deleteUserProfile(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserProfle(@PathVariable UUID id, Authentication authentication) {
        log.warn("Запрос на удаление пользователя c id {} администратором", id);
        userService.deleteUserProfile(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public UserProfileDto getUserProfile(@PathVariable UUID id) {
        log.info("Запрос получение пользователя с id: {}", id);
        return userService.getUserProfileWithAdvertisements(id);
    }

//    @GetMapping
//    public List<UserProfileDto> getUserProfiles(){
//        return userService.getUserProfiles();
//    }

    @PutMapping(path="/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserProfileDto updateUserProfile(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        log.warn("Запрос обновления пользователя с id {}", id);
        return userService.updateUserProfile(id, request);
    }
}
