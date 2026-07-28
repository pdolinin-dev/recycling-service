package com.example.recycling_service.controller;

import com.example.recycling_service.config.SecurityConfig;

import com.example.recycling_service.dto.CategoryDto;
import com.example.recycling_service.dto.PageResponse;
import com.example.recycling_service.dto.Request.CreateAdvertisementRequest;
import com.example.recycling_service.dto.Response.AdvertisementResponse;
import com.example.recycling_service.exception.ForbiddenException;
import com.example.recycling_service.security.JwtTokenProvider;
import com.example.recycling_service.service.AdvertisementService;
import com.example.recycling_service.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(AdvertisementController.class)
class AdvertisementControllerTest {

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

    @MockitoBean
    AdvertisementService advertisementService;

    @Test
    @DisplayName("GET /advertisement - 200 и список advertisementReponse")
    void getAllAdvertisement_success_return200() throws Exception {
        AdvertisementResponse response1 = new AdvertisementResponse();
        response1.setId(UUID.randomUUID());
        response1.setTitle("test_title1");
        response1.setCategories(new HashSet<>());
        response1.setUserId(UUID.randomUUID());
        AdvertisementResponse response2 = new AdvertisementResponse();
        response2.setId(UUID.randomUUID());
        response2.setTitle("test_title2");
        response2.setCategories(new HashSet<>());
        response2.setUserId(UUID.randomUUID());
        PageResponse<AdvertisementResponse> fakeResult = new PageResponse<>();
        fakeResult.setContent(List.of(response1, response2));
        fakeResult.setPageSize(3);
        fakeResult.setTotalElements(2);
        fakeResult.setPageNumber(1);

        when(advertisementService.findAll(anyInt(), anyInt()))
                .thenReturn(fakeResult);

        mockMvc.perform(get("/api/v1/advertisements?pageNumber=1&pageSize=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(response1.getId().toString()))
                .andExpect(jsonPath("$.content[0].title").value("test_title1"))
                .andExpect(jsonPath("$.content[0].userId").value(response1.getUserId().toString()))
                .andExpect(jsonPath("$.pageNumber").value(1))
                .andExpect(jsonPath("$.pageSize").value(3))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("POST /advertisement - 200")
    void createAdvertisement_success_return201() throws Exception{
        CategoryDto categoryDto = new CategoryDto();
        UUID categoryId = UUID.randomUUID();
        categoryDto.setId(categoryId);
        categoryDto.setName("category_name");
        Set<CategoryDto> categoryDtoSet = new HashSet<>();
        categoryDtoSet.add(categoryDto);

        CreateAdvertisementRequest request = new CreateAdvertisementRequest();
        request.setTitle("test_title1");
        request.setDescription("test description");
        Set<UUID> categoryIdsSet = new HashSet<>();
        categoryIdsSet.add(categoryId);
        request.setCategoryIds(categoryIdsSet);
        request.setPrice(BigDecimal.valueOf(1000));

        AdvertisementResponse response = new AdvertisementResponse();
        response.setId(UUID.randomUUID());
        response.setTitle("test_title1");
        response.setDescription("test description");
        response.setCategories(categoryDtoSet);
        response.setPrice(BigDecimal.valueOf(1000));
        response.setUserId(UUID.randomUUID());

        when(advertisementService.createAdvertisement(any(), eq("testuser")))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/advertisements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId().toString()));
    }

    @Test
    @DisplayName("POST /advertisement без токена - 401")
    void createAdvertisement_withoutToken_return401() throws Exception{
        mockMvc.perform(post("/api/v1/advertisements")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"test\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /advertisements/{id} без токена - 401")
    void deleteAdvertisement_withoutToken_return401() throws Exception {
        mockMvc.perform(delete("/api/v1/advertisements/", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "wronguser")
    @DisplayName("DELETE /advertisements/{id} с токеном, но не тот пользователь - 403")
    void deleteAdvertisement_withTokenButUserIsNotAnOwner_return403() throws Exception {
        doThrow(new ForbiddenException("wronguser"))
                .when(advertisementService)
                        .deleteAdvertisement(any(), eq("wronguser"));

        mockMvc.perform(delete("/api/v1/advertisement/", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}