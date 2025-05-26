package com.example.recycling_service.controller;

import com.example.recycling_service.dto.UpdateUserRequest;
import com.example.recycling_service.dto.UserProfileDto;
import com.example.recycling_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService; // Оставляем только сервис

    @GetMapping("/profile")
    public UserProfileDto getUserProfile(Authentication authentication) {
        return userService.getUserProfileWithAdvertisements(authentication.getName());
    }
//    @GetMapping("/profile")
//    public UserProfileDto getUserProfile(Authentication authentication) {
//        // Временное решение - используем дефолтного пользователя, если аутентификация null
//        String username = (authentication != null) ? authentication.getName() : "user1234";
//        return userService.getUserProfileWithAdvertisements(username);
//    }

    @GetMapping("/{id}")
    public UserProfileDto getUserProfile(@PathVariable Long id) {
        return userService.getUserProfileWithAdvertisements(id);
    }

    @PutMapping(path="/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserProfileDto updateUserProfile(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateUserProfile(id, request);
    }
}