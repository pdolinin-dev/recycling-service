package com.example.recycling_service.controller;

import com.example.recycling_service.dto.Request.UpdateUserRequest;
import com.example.recycling_service.dto.UserProfileDto;

import com.example.recycling_service.service.UserService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService; // Оставляем только сервис

    @GetMapping("/profile")
    public UserProfileDto getUserProfile(Authentication authentication) {
        log.info("Запрос профиля пользователя {}", authentication.getName());
        UserProfileDto response = userService.getUserProfileWithAdvertisements(authentication.getName());
        log.info("Получен профиль пользователя login: [{}] id: [{}]", response.getLogin(), response.getId());
        return response;
    }

    @Transactional
    @DeleteMapping()
    public ResponseEntity<Void> deleteUserProfile(Authentication authentication) {
        log.warn("Запрос на удаление пользователя с логином: [{}]", authentication.getName());
        userService.deleteUserProfile(authentication.getName());
        log.warn("Удален пользователь с логином: [{}]", authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserProfle(@PathVariable UUID id, Authentication authentication) {
        log.warn("Запрос на удаление пользователя c id [{}] администратором: [{}]", id, authentication.getName());
        userService.deleteUserProfile(id, authentication.getName());
        log.warn("Пользователь [{}] удален администратором: [{}]", id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public UserProfileDto getUserProfile(@PathVariable UUID id) {
        log.info("Запрос получение пользователя с id: [{}]", id);
        UserProfileDto response = userService.getUserProfileWithAdvertisements(id);
        log.info("Получен профиль пользователя с id: [{}]", id);
        return response;
    }

//    @GetMapping
//    public List<UserProfileDto> getUserProfiles(){
//        return userService.getUserProfiles();
//    }

    @PutMapping(path="/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserProfileDto updateUserProfile(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        log.warn("Запрос обновления пользователя с id [{}]", id);
        UserProfileDto response = userService.updateUserProfile(id, request);
        log.warn("Обновлен пользователь с id [{}]", id);
        return response;
    }
}
