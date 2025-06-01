package com.example.recycling_service.controller;

import com.example.recycling_service.dto.UpdateUserRequest;
import com.example.recycling_service.dto.UserProfileDto;
import com.example.recycling_service.model.User;
import com.example.recycling_service.repository.ChatMessageRepository;
import com.example.recycling_service.repository.UserRepository;
import com.example.recycling_service.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService; // Оставляем только сервис
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    private static final Long DELETED_USER_ID = 27L;

    @GetMapping("/profile")
    public UserProfileDto getUserProfile(Authentication authentication) {
        return userService.getUserProfileWithAdvertisements(authentication.getName());
    }

    @Transactional
    @DeleteMapping()
    public ResponseEntity<Void> deleteUserProfile(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        chatMessageRepository.updateUserSenderReferencesToDeleted(user.getId(), DELETED_USER_ID);
        chatMessageRepository.updateUserReceiverReferencesToDeleted(user.getId(), DELETED_USER_ID);
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserProfle(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        chatMessageRepository.updateUserSenderReferencesToDeleted(user.getId(), DELETED_USER_ID);
        chatMessageRepository.updateUserReceiverReferencesToDeleted(user.getId(), DELETED_USER_ID);
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
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

    @GetMapping
    public List<UserProfileDto> getUserProfiles(){
        return userService.getUserProfiles();
    }

    @PutMapping(path="/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserProfileDto updateUserProfile(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateUserProfile(id, request);
    }
}