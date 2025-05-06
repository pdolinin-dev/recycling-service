package com.example.recycling_service.controller;

import com.example.recycling_service.dto.UserProfileDto;
import com.example.recycling_service.model.Advertisement;
import com.example.recycling_service.model.User;
import com.example.recycling_service.repository.AdvertisementRepository;
import com.example.recycling_service.repository.UserRepository;
import com.example.recycling_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService; // Оставляем только сервис

//    @GetMapping("/profile")
//    public UserProfileDto getUserProfile(Authentication authentication) {
//        return userService.getUserProfileWithAdvertisements(authentication.getName());
//    }
    @GetMapping("/profile")
    public UserProfileDto getUserProfile(Authentication authentication) {
        // Временное решение - используем дефолтного пользователя, если аутентификация null
        String username = (authentication != null) ? authentication.getName() : "user1234";
        return userService.getUserProfileWithAdvertisements(username);
    }

    @GetMapping("/{id}")
    public UserProfileDto getUserProfile(@PathVariable Long id) {
        return userService.getUserProfileWithAdvertisements(id);
    }

    @PutMapping(path="/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserProfileDto updateUserProfile(@PathVariable Long id, @RequestBody UserProfileDto userProfileDto) {
        return
    }
}