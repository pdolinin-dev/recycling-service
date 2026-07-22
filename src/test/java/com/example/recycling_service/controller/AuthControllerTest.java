package com.example.recycling_service.controller;

import com.example.recycling_service.config.SecurityConfig;
import com.example.recycling_service.dto.Request.LoginRequest;
import com.example.recycling_service.dto.Request.RegisterRequest;
import com.example.recycling_service.dto.Response.JwtResponse;
import com.example.recycling_service.exception.ConflictException;
import com.example.recycling_service.security.JwtAuthenticationFilter;
import com.example.recycling_service.security.JwtTokenProvider;
import com.example.recycling_service.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AuthService authService;

    @MockitoBean
    JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    UserDetailsService userDetailsService;

    @Test
    @DisplayName("POST /login - 200 и тело с токеном")
    void login_success_return200() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setLogin("test_login");
        request.setPassword("test_password");

        JwtResponse fakeResponse = new JwtResponse("test_login", "ROLE_USER", "fake.jwt.token");
        when(authService.login(any())).thenReturn(fakeResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.login").value("test_login"))
            .andExpect(jsonPath("$.jwt").value("fake.jwt.token"))
            .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    @DisplayName("POST /login - 400 при пустом поле")
    void login_invalidBody_return400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /register - 200 и тело с токеном")
    void register_success_return200() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("test_name");
        request.setEmail("testemail@test.ru");
        request.setLogin("new_login");
        request.setPassword("test_password");

        JwtResponse fakeResponse = new JwtResponse("new_login", "ROLE_USER", "fake.jwt.token");
        when(authService.register(any())).thenReturn(fakeResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("new_login"))
                .andExpect(jsonPath("$.jwt").value("fake.jwt.token"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    @DisplayName("POST /register - 400 при пустом поле")
    void register_invalidBody_return400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /register - 409 при дубле логинов")
    void register_loginConflict_return409() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("test_name");
        request.setEmail("testemail@test.ru");
        request.setLogin("new_login");
        request.setPassword("test_password");

        when(authService.register(any()))
                .thenThrow(new ConflictException("Пользователь", "login"));

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}