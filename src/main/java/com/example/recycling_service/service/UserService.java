package com.example.recycling_service.service;

import com.example.recycling_service.dto.Request.UpdateUserRequest;
import com.example.recycling_service.dto.Response.AdvertisementResponse;
import com.example.recycling_service.dto.UserProfileDto;
import com.example.recycling_service.exception.ForbiddenException;
import com.example.recycling_service.model.Advertisement;
import com.example.recycling_service.model.Enum.Role;
import com.example.recycling_service.model.User;
import com.example.recycling_service.repository.AdvertisementRepository;
import com.example.recycling_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;
    /***
     * Обновление профиля
     * @param request UpdateUserRequest
     * @return UserProfileDto
     */
    public UserProfileDto updateUserProfile(UUID id, @Valid UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User" + id + " not found"));

        //Меняем значение только, если в request не Null
        if (request.getLogin() != null) {
            user.setLogin(request.getLogin());
            log.info("Обновлен логин [{}] пользователя {}", request.getLogin(), id);
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
            log.info("Обновлен email [{}] пользователя {}", request.getEmail(), id);
        }
        if (request.getName() != null) {
            user.setName(request.getName());
            log.info("Обновлено имя [{}] пользователя {}", request.getName(), id);
        }

        List<AdvertisementResponse> ads = getAdvertisementsByUserId(user.getId());

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

    /***
     *
     * @param login Логин удаляемого пользователя
     */
    public void deleteUserProfile(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    log.error("Пользователь с login: {} не найден", login);
                    return new UsernameNotFoundException("Пользователь с login: " + login + " не найден");
                });

        userRepository.delete(user);
    }

    /***
     *
     * @param deletableUserId Id удаляемого пользователя
     * @param login Логин текущего пользователя
     */
    public void deleteUserProfile(UUID deletableUserId, String login) {
        User currentUser = userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    log.error("Пользователь с login: {} не найден", login);
                    return new UsernameNotFoundException("Пользователь с login: " + login + " не найден");
                });

        // Действие доступно только админу
        if (!currentUser.getRole().equals(Role.ADMIN)) {
            log.error("У роли пользователя {} недостаточно прав для управления профилем с id: {}", currentUser.getLogin(), deletableUserId);
            throw new ForbiddenException(currentUser.getLogin());
        }

        User deletableUser = userRepository.findById(deletableUserId)
                .orElseThrow(() -> {
                    log.error("Пользователь с id: {} не найден", deletableUserId);
                    return new UsernameNotFoundException("Пользователь с id: " + deletableUserId + " не найден");
                });

        userRepository.delete(deletableUser);
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    log.warn("Пользователь с login: {} не найден", login);
                    return new UsernameNotFoundException("Пользователь с login: " + login +" не найден");
                    });

        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    public UserProfileDto getUserProfileWithAdvertisements(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    log.warn("Пользователь с login: {} не найден", login);
                    return new UsernameNotFoundException("Пользователь с login: " + login +" не найден");
                });

        List<AdvertisementResponse> ads = getAdvertisementsByUserId(user.getId());

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
                .orElseThrow(() -> {
                    log.warn("Пользователь с id: {} не найден", userId);
                    return new UsernameNotFoundException("Пользователь с id: " + userId +" не найден");
                });

        List<AdvertisementResponse> ads = getAdvertisementsByUserId(user.getId());

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
            List<AdvertisementResponse> ads = getAdvertisementsByUserId(user.getId());

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
                .orElseThrow(() -> {
                    log.warn("Пользователь с id: {} не найден", userId);
                    return new UsernameNotFoundException("Пользователь с id: " + userId +" не найден");
                });
//        user.setAvatarPath(avatarPath);
        return toUserProfileDto(userRepository.save(user));
    }

    private UserProfileDto toUserProfileDto(User user) {
        List<AdvertisementResponse> ads = getAdvertisementsByUserId(user.getId());

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

    private List<AdvertisementResponse> getAdvertisementsByUserId(UUID id) {
        return advertisementRepository.findByUserId(id)
                .stream().map(AdvertisementResponse::new).toList();
    }
}