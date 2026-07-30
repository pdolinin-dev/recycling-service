package com.example.recycling_service.integration;

import com.example.recycling_service.dto.Request.CreateAdvertisementRequest;
import com.example.recycling_service.model.Category;
import com.example.recycling_service.repository.CategoryRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PositiveScenarioWithAdvertisement extends BaseIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Сквозной сценарий: регистрация -> логин -> создание объявления -> просмотр в списке")
    void fullAdvertisementScenario() throws Exception {
        // Создание категории
        Category category = categoryRepository.save(new Category("Категория 1"));

        // Регистрация
        String jwt = registerAndGetToken("tester@email.com", "tester_login", "tester_pass");

        CreateAdvertisementRequest createRq = new CreateAdvertisementRequest();
        createRq.setTitle("Телефон");
        createRq.setDescription("Продам телефон совсем недорого, отличный товар");
        createRq.setPrice(BigDecimal.valueOf(5000));
        createRq.setCategoryIds(Set.of(category.getId()));

        String createdBody = mockMvc.perform(post("/api/v1/advertisements")
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Телефон"))
                .andReturn().getResponse().getContentAsString();

        UUID createdId = UUID.fromString(JsonPath.read(createdBody, "$.id"));

        mockMvc.perform(get("/api/v1/advertisements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(createdId.toString()))
                .andExpect(jsonPath("$.content[0].title").value("Телефон"));
    }
}
