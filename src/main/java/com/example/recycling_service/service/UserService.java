package com.example.recycling_service.service;

import com.example.recycling_service.dto.Request.UpdateUserRequest;
import com.example.recycling_service.dto.UserProfileDto;
import com.example.recycling_service.model.Advertisement;
import com.example.recycling_service.model.User;
import com.example.recycling_service.repository.AdvertisementRepository;
import com.example.recycling_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;

    public UserProfileDto updateUserProfile(UUID id, @Valid UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with id:" + id));

        //Change only received entitys (if it not null)
        if (request.getUsername() != null) {
            user.setLogin(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getName() != null) {
            user.setName(request.getName());
        }

        List<Advertisement> ads = advertisementRepository.findByUserId(request.getId());

//        if (request.getAvatarPath() != null) user.setAvatarPath(request.getAvatarPath());
//
        User updatedUser = userRepository.save(user);
//
        return new UserProfileDto(
                updatedUser.getId(),
                updatedUser.getLogin(),
                updatedUser.getName(),
                updatedUser.getCreatedAt(),
                updatedUser.getUpdatedAt(),
                updatedUser.getEmail(),
                ads
//                updatedUser.getAvatarPath()
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User" + username +"not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    public UserProfileDto getUserProfileWithAdvertisements(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User" + username + " not found"));

        List<Advertisement> ads = advertisementRepository.findByUserId(user.getId());

        return new UserProfileDto(
                user.getId(),
                user.getLogin(),
                user.getName(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getEmail(),
                ads
        );
    }

    public UserProfileDto getUserProfileWithAdvertisements(UUID userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User" + userId + " not found"));

        List<Advertisement> ads = advertisementRepository.findByUserId(userId);

        return new UserProfileDto(
                user.getId(),
                user.getLogin(),
                user.getName(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getEmail(),
                ads
//                user.getAvatarPath()
        );
    }

    public List<UserProfileDto> getUserProfiles() {
        List<User> users = userRepository.findAll();
        List<UserProfileDto> userProfileDtos = new ArrayList<>(users.size());
        for (User user : users) {
            List<Advertisement> ads = advertisementRepository.findByUserId(user.getId());
            userProfileDtos.add(
                    new UserProfileDto(
                            user.getId(),
                            user.getLogin(),
                            user.getName(),
                            user.getCreatedAt(),
                            user.getUpdatedAt(),
                            user.getEmail(),
                            ads
//                            user.getAvatarPath()
                    ));
        }
        return userProfileDtos;
    }

    @Transactional
    public UserProfileDto updateAvatar(UUID userId, String avatarPath) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//        user.setAvatarPath(avatarPath);
        return toUserProfileDto(userRepository.save(user));
    }

    private UserProfileDto toUserProfileDto(User user) {
        List<Advertisement> ads = advertisementRepository.findByUserId(user.getId());
        UserProfileDto dto = new UserProfileDto(
                user.getId(),
                user.getLogin(),
                user.getName(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getEmail(),
                ads
//                user.getAvatarPath()
        );
        return dto;
    }
}