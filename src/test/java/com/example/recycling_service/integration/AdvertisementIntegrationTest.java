package com.example.recycling_service.integration;

import com.example.recycling_service.dto.CategoryDto;
import com.example.recycling_service.dto.Request.CreateAdvertisementRequest;
import com.example.recycling_service.dto.Response.AdvertisementResponse;
import com.example.recycling_service.model.Category;
import com.example.recycling_service.repository.AdvertisementRepository;
import com.example.recycling_service.repository.CategoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdvertisementIntegrationTest extends BaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AdvertisementRepository advertisementRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("POST /advertisement - Успешное создание объявления - 200")
    void createAdvertisement_success_return200() throws Exception {
        // регистрация пользователя
        String jwt = registerAndGetToken("user@test.com", "user_login", "user_pass");

        // Создание категории
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName("Test Category");
        categoryRepository.save(category);

        CreateAdvertisementRequest request = new CreateAdvertisementRequest();
        request.setPrice(BigDecimal.valueOf(1000));
        request.setDescription("Test description of advertisement");
        request.setTitle("Test title");
        request.setCategoryIds(Set.of(category.getId()));

        String responseBody = mockMvc.perform(post("/api/v1/advertisements")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("Test title"))
                .andExpect(jsonPath("$.description").value("Test description of advertisement"))
                .andExpect(jsonPath("$.price").value(1000))
                .andExpect(jsonPath("$.categories").isArray())
                .andReturn().getResponse().getContentAsString();

        UUID createdId = UUID.fromString(JsonPath.read(responseBody, "$.id"));
        assertTrue(advertisementRepository.existsById(createdId));
    }

    @Test
    @DisplayName("GET /advertisement - Получение списка объявлений - 200")
    void getAdvertisements_success_return200() throws Exception{
        // регистрация пользователя
        String jwt = registerAndGetToken("user@test.com", "user_login", "user_pass");

        // Создание категории
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName("Test Category");
        categoryRepository.save(category);

        CreateAdvertisementRequest request = new CreateAdvertisementRequest();
        request.setPrice(BigDecimal.valueOf(1000));
        request.setDescription("Test description of advertisement");
        request.setTitle("Test title");
        request.setCategoryIds(Set.of(category.getId()));

        String responseBody = mockMvc.perform(post("/api/v1/advertisements")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("Test title"))
                .andExpect(jsonPath("$.description").value("Test description of advertisement"))
                .andExpect(jsonPath("$.price").value(1000))
                .andExpect(jsonPath("$.categories").isArray())
                .andReturn().getResponse().getContentAsString();

        UUID createdId = UUID.fromString(JsonPath.read(responseBody, "$.id"));

        mockMvc.perform(get("/api/v1/advertisements?pageNumber=1&pageSize=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").isNotEmpty())
                .andExpect(jsonPath("$.content[0].title").value("Test title"))
                .andExpect(jsonPath("$.content[0].description").value("Test description of advertisement"))
                .andExpect(jsonPath("$.content[0].price").value(1000))
                .andExpect(jsonPath("$.content[0].categories").isArray())
                .andExpect(jsonPath("$.pageNumber").value(1))
                .andExpect(jsonPath("$.pageSize").value(3))
                .andExpect(jsonPath("$.totalElements").value(2));
    }
}
