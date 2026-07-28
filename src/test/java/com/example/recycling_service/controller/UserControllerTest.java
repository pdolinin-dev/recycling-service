package com.example.recycling_service.controller;

import com.example.recycling_service.config.SecurityConfig;
import com.example.recycling_service.dto.Request.UpdateUserRequest;
import com.example.recycling_service.dto.Response.AdvertisementResponse;
import com.example.recycling_service.dto.UserProfileDto;

import com.example.recycling_service.exception.ForbiddenException;
import com.example.recycling_service.exception.NotFoundException;
import com.example.recycling_service.security.JwtTokenProvider;
import com.example.recycling_service.service.AuthService;
import com.example.recycling_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AuthService authService;

    @MockitoBean
    JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    UserService userService;

    private static UserProfileDto userDto;
    private static AdvertisementResponse advertisement;
    private static List<AdvertisementResponse> advertisementList;
    private static UUID userId;
    private static UpdateUserRequest request;

    @BeforeEach
    void setUp() {
        request = new UpdateUserRequest();

        advertisement = new AdvertisementResponse();
        advertisement.setId(UUID.randomUUID());
        advertisement.setTitle("test_title1");
        advertisement.setCategories(new HashSet<>());
        advertisement.setUserId(UUID.randomUUID());

        userId = UUID.randomUUID();
        userDto = new UserProfileDto(
                userId,
                "user_login_test",
                "user_name_test",
                LocalDateTime.now(),
                LocalDateTime.now(),
                "test@test.test",
                List.of(advertisement)
        );
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("GET /profile - Получение собественного профиля - 2xx")
    void getUserProfile_success_200() throws Exception {
        when(userService.getUserProfileWithAdvertisements("testUser"))
                .thenReturn(userDto);

        mockMvc.perform(get("/api/v1/user/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.login").value("user_login_test"))
                .andExpect(jsonPath("$.name").value("user_name_test"))
                .andExpect(jsonPath("$.email").value("test@test.test"))
                .andExpect(jsonPath("$.advertisements").isArray())
                .andExpect(jsonPath("$.advertisements[0].id").value(advertisement.getId().toString()))
                .andExpect(jsonPath("$.advertisements[0].title").value("test_title1"))
                .andExpect(jsonPath("$.advertisements[0].userId").value(advertisement.getUserId().toString()));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("GET /profile - Получение собственного профиля - 404")
    void getUserProfile_userNotFound_return404() throws Exception {
        when(userService.getUserProfileWithAdvertisements(anyString()))
                .thenThrow(new NotFoundException("Пользователь", "login", "testuser"));

        mockMvc.perform(get("/api/v1/user/profile"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /profile - Получение собственного профиля - 401")
    void getUserProfile_userUnauthorised_return401() throws Exception {
        mockMvc.perform(get("/api/v1/user/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /users/{id} - Получение профиля пользователя - 200")
    void getUserProfileById_success_return200() throws Exception {
        when(userService.getUserProfileWithAdvertisements(userId))
                .thenReturn(userDto);

        mockMvc.perform(get("/api/v1/user/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.login").value("user_login_test"))
                .andExpect(jsonPath("$.name").value("user_name_test"))
                .andExpect(jsonPath("$.email").value("test@test.test"))
                .andExpect(jsonPath("$.advertisements").isArray())
                .andExpect(jsonPath("$.advertisements[0].id").value(advertisement.getId().toString()))
                .andExpect(jsonPath("$.advertisements[0].title").value("test_title1"))
                .andExpect(jsonPath("$.advertisements[0].userId").value(advertisement.getUserId().toString()));
    }

    @Test
    @DisplayName("GET /users/{id} - Получение профиля пользователя - 404")
    void getUserProfileById_userNotFound_return404() throws Exception {
        when(userService.getUserProfileWithAdvertisements(userId))
                .thenThrow(new NotFoundException("Пользователь", "id", userId));

        mockMvc.perform(get("/api/v1/user/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("DELETE /users - Удаление собственного профиля - 403")
    void deleteUserProfile_noUser_return403() throws Exception {
        doThrow(new ForbiddenException("testUser"))
                .when(userService)
                .deleteUserProfile("testUser");

        mockMvc.perform(delete("/api/v1/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /users - Удаление собственного профиля - 401")
    void deleteUserProfile_userIsUnauthorized_return401() throws Exception {
        mockMvc.perform(delete("/api/v1/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testAdmin", roles = "ADMIN")
    @DisplayName("DELETE /users/{id} - Удаление пользователя администратором - 404")
    void deleteUserProfileById_userNotFound_return404() throws Exception {
        doThrow(new NotFoundException("Пользователь", "id", userId))
                .when(userService)
                .deleteUserProfile(userId, "testAdmin");

        mockMvc.perform(delete("/api/v1/user/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    @DisplayName("DELETE /users/{id} - Удаление пользователя администратором - 403")
    void deleteUserProfileById_userIsNotAdmin_return403() throws Exception {
        doThrow(new ForbiddenException("Пользователь"))
                .when(userService)
                .deleteUserProfile(userId, "testUser");

        mockMvc.perform(delete("/api/v1/user/{id}", userId))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /users/{id} - Удаление пользователя администратором - 401")
    void deleteUserProfileById_userIsUnauthorized_return401() throws Exception {
        mockMvc.perform(delete("/api/v1/user/{id}", userId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("PUT /users/{id} - Обновление пользователя - 200")
    void updateUserProfile_success_return200() throws Exception {
        when(userService.updateUserProfile(eq(userId), any()))
                .thenReturn(userDto);

        mockMvc.perform(put("/api/v1/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.login").value("user_login_test"))
                .andExpect(jsonPath("$.name").value("user_name_test"))
                .andExpect(jsonPath("$.email").value("test@test.test"))
                .andExpect(jsonPath("$.advertisements").isArray())
                .andExpect(jsonPath("$.advertisements[0].id").value(advertisement.getId().toString()))
                .andExpect(jsonPath("$.advertisements[0].title").value("test_title1"))
                .andExpect(jsonPath("$.advertisements[0].userId").value(advertisement.getUserId().toString()));
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("PUT /users/{id} - Обновление пользователя - 404")
    void updateUserProfile_userNotFound_return404() throws Exception {
        when(userService.updateUserProfile(any(), any()))
                .thenThrow(new NotFoundException("Пользователь", "id", userId));

        mockMvc.perform(put("/api/v1/user/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /users/{id} - Обновление пользователя - 401")
    void updateUserProfile_userIsUnauthorized_return401() throws Exception {
        mockMvc.perform(put("/api/v1/user/{id}", userId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("PUT /users/{id} - Обновление пользователя - 403")
    void updateUserProfile_userIsForbidden_return403() throws Exception {
        when(userService.updateUserProfile(eq(userId), any()))
                .thenThrow(new ForbiddenException(userDto.getName()));

        mockMvc.perform(put("/api/v1/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}