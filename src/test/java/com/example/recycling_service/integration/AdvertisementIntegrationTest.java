package com.example.recycling_service.integration;

import com.example.recycling_service.dto.Request.CreateAdvertisementRequest;
import com.example.recycling_service.dto.Request.FilterAdvertisementRequest;
import com.example.recycling_service.model.Category;
import com.example.recycling_service.repository.AdvertisementRepository;
import com.example.recycling_service.repository.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
        Category category = new Category("Test Category");
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
        Category category = new Category("Test Category");
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

        mockMvc.perform(get("/api/v1/advertisements?pageSize=3&pageNumber=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(createdId.toString()))
                .andExpect(jsonPath("$.content[0].title").value("Test title"))
                .andExpect(jsonPath("$.content[0].description").value("Test description of advertisement"))
                .andExpect(jsonPath("$.content[0].price").value(1000))
                .andExpect(jsonPath("$.content[0].categories").isArray())
                .andExpect(jsonPath("$.pageNumber").value(1))
                .andExpect(jsonPath("$.pageSize").value(3))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /advertisement/{id} - Получение информации об объявлении - 200")
    void getAdvertisementById_success_return200() throws Exception{
        // регистрация пользователя
        String jwt = registerAndGetToken("user@test.com", "user_login", "user_pass");

        // Создание категории
        Category category = new Category("Test Category");
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

        mockMvc.perform(get("/api/v1/advertisements/{id}", createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdId.toString()))
                .andExpect(jsonPath("$.title").value("Test title"))
                .andExpect(jsonPath("$.description").value("Test description of advertisement"))
                .andExpect(jsonPath("$.price").value(1000))
                .andExpect(jsonPath("$.categories").isArray());
    }

    @Test
    @DisplayName("GET /categories - Получение списка категорий - 200")
    void getAllCategories_success_return200() throws Exception{
        Category category = new Category("Test Category 1");
        Category category1 = new Category("Test Category 2");
        Category category2 = new Category("Test Category 3");
        categoryRepository.save(category);
        categoryRepository.save(category1);
        categoryRepository.save(category2);

        mockMvc.perform(get("/api/v1/advertisements/categories"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("[0].name").value("Test Category 1"))
                .andExpect(jsonPath("[1].name").value("Test Category 2"))
                .andExpect(jsonPath("[2].name").value("Test Category 3"));
    }

    @Test
    @DisplayName("GET /by-categories - Получение списка объявлений по категориям - 200")
    void getCategoriesById_success_return200() throws Exception {
        // регистрация пользователя
        String jwt = registerAndGetToken("user@test.com", "user_login", "user_pass");

        // Создание категории
        Category category = new Category("Test Category");
        categoryRepository.save(category);

        String categoryResponse = mockMvc.perform(get("/api/v1/advertisements/categories"))
                .andReturn().getResponse().getContentAsString();

        UUID categoryId = UUID.fromString(JsonPath.read(categoryResponse,"$[0].id"));

        CreateAdvertisementRequest request = new CreateAdvertisementRequest();
        request.setPrice(BigDecimal.valueOf(1000));
        request.setDescription("Test description of advertisement");
        request.setTitle("Test title");
        request.setCategoryIds(Set.of(categoryId));

        String responseBody = mockMvc.perform(post("/api/v1/advertisements")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UUID createdId = UUID.fromString(JsonPath.read(responseBody, "$.id"));
        assertTrue(advertisementRepository.existsById(createdId));

        FilterAdvertisementRequest filterRequest = new FilterAdvertisementRequest();
        filterRequest.setCategoryIds(List.of(categoryId));

        mockMvc.perform(post("/api/v1/advertisements/by-categories?pageSize=3&pageNumber=1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(createdId.toString()))
                .andExpect(jsonPath("$.content[0].title").value("Test title"))
                .andExpect(jsonPath("$.content[0].description").value("Test description of advertisement"))
                .andExpect(jsonPath("$.content[0].price").value(1000))
                .andExpect(jsonPath("$.content[0].categories").isArray())
                .andExpect(jsonPath("$.pageNumber").value(1))
                .andExpect(jsonPath("$.pageSize").value(3))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
