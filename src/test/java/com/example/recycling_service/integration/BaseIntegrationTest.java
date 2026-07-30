package com.example.recycling_service.integration;

import com.example.recycling_service.dto.Request.LoginRequest;
import com.example.recycling_service.dto.Request.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    // Singleton: один контейнер на весь JVM, не пересоздаётся между тест-классами
    static final PostgreSQLContainer<?> postgres;

    static {
        System.setProperty("DOCKER_HOST", "tcp://localhost:2375");
        System.setProperty("TESTCONTAINERS_RYUK_DISABLED", "true");
        postgres = new PostgreSQLContainer<>("postgres:15")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
        postgres.start();
    }

    @DynamicPropertySource
    static void overrideDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    protected String registerAndGetToken(String email, String login, String password) throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail(email); req.setLogin(login);
        req.setPassword(password); req.setName("Test User");

        String body = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(body, "$.jwt");
    }

    protected String loginAsAdminAndGetToken() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setLogin("test_admin");
        req.setPassword("Admin_password_123");

        String body = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(body, "$.jwt");
    }
}
