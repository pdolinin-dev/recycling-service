package com.example.recycling_service.service;

import com.example.recycling_service.dto.UpdateUserRequest;
import com.example.recycling_service.dto.UserProfileDto;
import com.example.recycling_service.model.Advertisement;
import com.example.recycling_service.model.User;
import com.example.recycling_service.repository.AdvertisementRepository;
import com.example.recycling_service.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;

    public UserProfileDto updateUserProfile(Long id, @Valid UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with id:" + id));

        //Change only received entitys (if it not null)
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getName() != null) {
            user.setName(request.getName());
        }

        List<Advertisement> ads = advertisementRepository.findByUserId(request.getId());

        User updatedUser = userRepository.save(user);

        return new UserProfileDto(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getCreatedAt(),
                updatedUser.getUpdatedAt(),
                updatedUser.getPassword(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getRole(),
                ads
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User" + username +"not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }

    public UserProfileDto getUserProfileWithAdvertisements(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User" + username + " not found"));

        List<Advertisement> ads = advertisementRepository.findByUserId(user.getId());

        return new UserProfileDto(
                user.getId(),
                user.getUsername(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getPassword(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                ads
        );
    }

    public UserProfileDto getUserProfileWithAdvertisements(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User" + userId + " not found"));

        List<Advertisement> ads = advertisementRepository.findByUserId(userId);

        return new UserProfileDto(
                user.getId(),
                user.getUsername(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getPassword(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                ads
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
                            user.getUsername(),
                            user.getCreatedAt(),
                            user.getUpdatedAt(),
                            user.getPassword(),
                            user.getName(),
                            user.getEmail(),
                            user.getRole(),
                            ads
                    ));
        }
        return userProfileDtos;
    }
}