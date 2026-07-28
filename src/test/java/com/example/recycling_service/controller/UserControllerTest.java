package com.example.recycling_service.controller;

import com.example.recycling_service.config.SecurityConfig;
import com.example.recycling_service.dto.Response.AdvertisementResponse;
import com.example.recycling_service.dto.UserProfileDto;

import com.example.recycling_service.exception.NotFoundException;
import com.example.recycling_service.repository.UserRepository;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @BeforeEach
    void setUp() {
        advertisement = new AdvertisementResponse();
        advertisement.setId(UUID.randomUUID());
        advertisement.setTitle("test_title1");
        advertisement.setCategories(new HashSet<>());
        advertisement.setUserId(UUID.randomUUID());

        userDto = new UserProfileDto(
                UUID.randomUUID(),
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
                .andExpect(jsonPath("$.id").value(userDto.getId().toString()))
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
                .thenThrow(new UsernameNotFoundException("Пользователь с login: " + "testUser" +" не найден"));

        mockMvc.perform(get("/api/v1/user/profile"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /profile - Получение собственного профиля - 401")
    void getUserProfile_userNotFound_return401() throws Exception {
        mockMvc.perform(get("/api/v1/user/profile"))
                .andExpect(status().isUnauthorized());
    }
}