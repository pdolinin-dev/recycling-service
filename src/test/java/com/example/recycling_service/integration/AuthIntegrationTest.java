package com.example.recycling_service.integration;

import com.example.recycling_service.dto.Request.LoginRequest;
import com.example.recycling_service.dto.Request.RegisterRequest;
import com.example.recycling_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthIntegrationTest extends BaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("POST /register - успешная регистрация - 200")
    void register_success_return200() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setLogin("new_login");
        request.setPassword("password123");
        request.setEmail("new@test.com");
        request.setName("Test User");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("new_login"))
                .andExpect(jsonPath("$.jwt").isNotEmpty());

        assertTrue(userRepository.existsByLogin("new_login"));
    }

    @Test
    @DisplayName("POST /login - успешная авторизация - 200")
    void login_success_return200() throws Exception {
        // Регистрируем пользователя
        RegisterRequest request = new RegisterRequest();
        request.setLogin("new_login");
        request.setPassword("password123");
        request.setEmail("new@test.com");
        request.setName("Test User");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Логинимся под этим же пользователем
        LoginRequest login = new LoginRequest();
        login.setLogin("new_login");
        login.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("new_login"))
                .andExpect(jsonPath("$.jwt").isNotEmpty());
    }
}
