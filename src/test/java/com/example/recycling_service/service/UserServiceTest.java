package com.example.recycling_service.service;

import com.example.recycling_service.dto.Request.UpdateUserRequest;
import com.example.recycling_service.dto.UserProfileDto;
import com.example.recycling_service.exception.ForbiddenException;
import com.example.recycling_service.exception.NotFoundException;
import com.example.recycling_service.model.Advertisement;
import com.example.recycling_service.model.Enum.Role;
import com.example.recycling_service.model.User;
import com.example.recycling_service.repository.AdvertisementRepository;
import com.example.recycling_service.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Captor;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    AdvertisementRepository advertisementRepository;

    @Captor
    ArgumentCaptor<User> userArgumentCaptor;

    @InjectMocks
    UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setLogin("test_login_123");
        user.setPassword("test_passwordhash_123");
        user.setEmail("test_email_123");
        user.setName("test_name_123");
        user.setRole(Role.USER);
    }

    @Test
    @DisplayName("Получение Spring.Security.UserDetails")
    void loadByUsername_success() {
        when(userRepository.findByLogin(user.getLogin()))
                .thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername("test_login_123");

        assertThat(result.getUsername()).isEqualTo("test_login_123");
        assertThat(result.getPassword()).isEqualTo("test_passwordhash_123");
        assertThat(result.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("Получение Spring.Security.UserDetails: UserNotFoundException")
    void loadByUsername_NotFoundException() {
        when(userRepository.findByLogin(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("test_login_123"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Получение профиля по логину")
    void getUserProfileWithAdvertisements_success_login() {
        when(userRepository.findByLogin(user.getLogin()))
                .thenReturn(Optional.of(user));

        List<Advertisement> advertisementList = new ArrayList<>();
        when(advertisementRepository.findByUserId(user.getId()))
                .thenReturn(advertisementList);

        UserProfileDto userProfileDto = userService.getUserProfileWithAdvertisements("test_login_123");

        assertThat(userProfileDto.getLogin()).isEqualTo("test_login_123");
        assertThat(userProfileDto.getName()).isEqualTo("test_name_123");
        assertThat(userProfileDto.getId()).isEqualTo(user.getId());
        assertThat(userProfileDto.getEmail()).isEqualTo("test_email_123");
    }

    @Test
    @DisplayName("Получение профиля по логину: NotFoundException")
    void getUserProfileWithAdvertisements_NotFoundException_login() {
        when(userRepository.findByLogin(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserProfileWithAdvertisements("test_login_123"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Получение профиля по id")
    void getUserProfileWithAdvertisements_success_id() {
        when(userRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(user));

        List<Advertisement> advertisementList = new ArrayList<>();
        when(advertisementRepository.findByUserId(user.getId()))
                .thenReturn(advertisementList);

        UserProfileDto userProfileDto = userService.getUserProfileWithAdvertisements(user.getId());

        assertThat(userProfileDto.getLogin()).isEqualTo("test_login_123");
        assertThat(userProfileDto.getName()).isEqualTo("test_name_123");
        assertThat(userProfileDto.getId()).isEqualTo(user.getId());
        assertThat(userProfileDto.getEmail()).isEqualTo("test_email_123");
    }

    @Test
    @DisplayName("Получение профиля по id: NotFoundException")
    void getUserProfileWithAdvertisements_NotFoundException_id() {
        when(userRepository.findByUserId(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserProfileWithAdvertisements(user.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Обновление профиля пользователя")
    void updateUserProfile_success() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("new_email_123");
        request.setName("new_name_123");

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        List<Advertisement> advertisementList = new ArrayList<>();
        when(advertisementRepository.findByUserId(user.getId()))
                .thenReturn(advertisementList);

        when(userRepository.save(user))
                .thenReturn(user);

        UserProfileDto userProfileDto = userService.updateUserProfile(user.getId(), request);

        verify(userRepository).save(userArgumentCaptor.capture());
        User updatedUser = userArgumentCaptor.getValue();

        assertThat(updatedUser.getLogin()).isEqualTo("test_login_123");
        assertThat(updatedUser.getName()).isEqualTo("new_name_123");
        assertThat(updatedUser.getId()).isEqualTo(user.getId());
        assertThat(updatedUser.getEmail()).isEqualTo("new_email_123");
    }

    @Test
    @DisplayName("Обновление профиля пользователя: NotFoundException")
    void updateUserProfile_NotFoundException() {
        UpdateUserRequest request = new UpdateUserRequest();
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUserProfile(userId, request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Удаления профиля пользователем")
    void deleteUserProfile_success() {
        when(userRepository.findByLogin(user.getLogin()))
                .thenReturn(Optional.of(user));

        userService.deleteUserProfile("test_login_123");

        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("Удаления профиля пользователем: ")
    void deleteUserProfile_NotFoundException() {
        when(userRepository.findByLogin(user.getLogin()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUserProfile("test_login_123"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Удаления профиля админом ")
    void deleteUserProfile_success_by_admin() {
        User admin = new User();
        admin.setId(UUID.randomUUID());
        admin.setRole(Role.ADMIN);
        admin.setLogin("test_admin_login_123");
        admin.setPassword("test_admin_passwordhash_123");
        admin.setEmail("test_admin_email_123");
        admin.setName("test_admin_name_123");

        when(userRepository.findByLogin("test_admin_login_123"))
                .thenReturn(Optional.of(admin));

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        userService.deleteUserProfile(user.getId(), admin.getLogin());
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("Удаления профиля админом: ForbiddenException")
    void deleteUserProfile_ForbiddenException_by_admin() {
        when(userRepository.findByLogin(any()))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.deleteUserProfile(user.getId(), "test_login"))
                .isInstanceOf(ForbiddenException.class);
    }
}