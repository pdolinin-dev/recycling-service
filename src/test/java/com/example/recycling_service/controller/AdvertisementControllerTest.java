package com.example.recycling_service.controller;

import com.example.recycling_service.config.SecurityConfig;

import com.example.recycling_service.dto.PageResponse;
import com.example.recycling_service.dto.Response.AdvertisementResponse;
import com.example.recycling_service.security.JwtTokenProvider;
import com.example.recycling_service.service.AdvertisementService;
import com.example.recycling_service.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        fakeResult.setPageSize(2);
        fakeResult.setTotalElements(2);
        fakeResult.setPageNumber(1);

        when(advertisementService.findAll(1, 20))
                .thenReturn(fakeResult);

        mockMvc.perform(get("/api/v1/advertisements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(response1.getId()))
                .andExpect(jsonPath("$.content[0].title").value("test_title1"))
                .andExpect(jsonPath("$.content[0].userId").value(response1.getUserId()))
                .andExpect(jsonPath("$.pageNumber").value("1"))
                .andExpect(jsonPath("$.pageSize").value("20"))
                .andExpect(jsonPath("$.totalElements").value("2"));
    }
}