package com.example.recycling_service.controller;

import com.example.recycling_service.config.SecurityConfig;
import com.example.recycling_service.dto.CategoryDto;
import com.example.recycling_service.dto.RecyclingPointDto;
import com.example.recycling_service.dto.Request.CreateRecyclingPointRequest;
import com.example.recycling_service.dto.Request.RecyclePointFilterRequest;
import com.example.recycling_service.exception.ForbiddenException;
import com.example.recycling_service.model.Type;
import com.example.recycling_service.security.JwtTokenProvider;
import com.example.recycling_service.service.AuthService;
import com.example.recycling_service.service.RecyclingPointService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(RecyclingPointController.class)
class RecyclingPointControllerTest {

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
    RecyclingPointService recyclingPointService;

    private static RecyclingPointDto recyclingPointDto;
    private static RecyclingPointDto recyclingPointDto1;
    private static CategoryDto categoryDto;
    private static Set<CategoryDto> categoryDtoSet;
    private static Type type;

    @BeforeEach
    void setUp() {
        type = new Type();
        type.setId(UUID.randomUUID());
        type.setName("test_name_1");

        categoryDto = new CategoryDto();
        categoryDto.setId(UUID.randomUUID());
        categoryDto.setName("test_category_name");

        categoryDtoSet = new HashSet<>();
        categoryDtoSet.add(categoryDto);

        recyclingPointDto = new RecyclingPointDto();
        recyclingPointDto.setId(UUID.randomUUID());
        recyclingPointDto.setName("test_name_1");
        recyclingPointDto.setAddress("test_address_1");
        recyclingPointDto.setLatitude(123.123);
        recyclingPointDto.setLongitude(444.44);
        recyclingPointDto.setPhoneNumber("+79999999999");
        recyclingPointDto.setType(type);
        recyclingPointDto.setCategories(categoryDtoSet);

        recyclingPointDto1 = new RecyclingPointDto();
        recyclingPointDto1.setId(UUID.randomUUID());
        recyclingPointDto1.setName("test_name_2");
        recyclingPointDto1.setAddress("test_address_2");
        recyclingPointDto1.setLatitude(31.111);
        recyclingPointDto1.setLongitude(555.555);
        recyclingPointDto1.setPhoneNumber("+711111111111");
        recyclingPointDto1.setType(type);
        recyclingPointDto1.setCategories(categoryDtoSet);
    }

    @Test
    @DisplayName("GET /recycling-points - Получение всех пунктов - 200")
    void getAllPoints_success_return200() throws Exception {
        when(recyclingPointService.getAllPoints())
                .thenReturn(List.of(recyclingPointDto, recyclingPointDto1));

        mockMvc.perform(get("/api/v1/recycling-points"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(recyclingPointDto.getId().toString()))
                .andExpect(jsonPath("$[0].name").value("test_name_1"))
                .andExpect(jsonPath("$[0].address").value("test_address_1"))
                .andExpect(jsonPath("$[0].latitude").value(123.123))
                .andExpect(jsonPath("$[0].longitude").value(444.44))
                .andExpect(jsonPath("$[0].phoneNumber").value("+79999999999"))
                .andExpect(jsonPath("$[0].type.id").value(type.getId().toString()))
                .andExpect(jsonPath("$[0].type.name").value("test_name_1"))
                .andExpect(jsonPath("$[0].categories").isArray());
    }

    @Test
    @DisplayName("GET /categories - Получение всех категорий - 200")
    void getAllCategories_success_return200() throws Exception {
        CategoryDto categoryDto1 = new CategoryDto(UUID.randomUUID(), "category_name_1");
        CategoryDto categoryDto2 = new CategoryDto(UUID.randomUUID(), "category_name_2");
        CategoryDto categoryDto3 = new CategoryDto(UUID.randomUUID(), "category_name_3");

        when(recyclingPointService.getAllCategories())
                .thenReturn(List.of(categoryDto1, categoryDto2, categoryDto3));

        mockMvc.perform(get("/api/v1/recycling-points/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(categoryDto1.getId().toString()))
                .andExpect(jsonPath("$[0].name").value(categoryDto1.getName()));
    }

    @Test
    @DisplayName("GET /recycling-points/{id} - Получение пункта приема - 200")
    void getRecyclingPointsById_success_200() throws Exception {
        when(recyclingPointService.getPointById(recyclingPointDto.getId()))
                .thenReturn(recyclingPointDto);

        mockMvc.perform(get("/api/v1/recycling-points/{id}", recyclingPointDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(recyclingPointDto.getId().toString()))
                .andExpect(jsonPath("$.name").value(recyclingPointDto.getName()))
                .andExpect(jsonPath("$.type.id").value(type.getId().toString()))
                .andExpect(jsonPath("$.type.name").value(type.getName()))
                .andExpect(jsonPath("$.address").value("test_address_1"))
                .andExpect(jsonPath("$.latitude").value(123.123))
                .andExpect(jsonPath("$.longitude").value(444.44))
                .andExpect(jsonPath("$.phoneNumber").value("+79999999999"))
                .andExpect(jsonPath("$.categories").isArray());

    }

    @Test
    @DisplayName("POST /recycling-points/by-categories - Получение списка пунктов по категориям - 200")
    void getRecyclingPointByCategories_success_200() throws Exception {
        RecyclePointFilterRequest request = new RecyclePointFilterRequest();
        UUID categoryId1 = UUID.randomUUID();
        UUID categoryId2 = UUID.randomUUID();
        request.setCategoryIds(List.of(categoryId1, categoryId2));

        when(recyclingPointService.getPointByCategory(request))
                .thenReturn(List.of(recyclingPointDto, recyclingPointDto1));

        mockMvc.perform(post("/api/v1/recycling-points/by-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(recyclingPointDto.getId().toString()))
                .andExpect(jsonPath("$[0].name").value(recyclingPointDto.getName()))
                .andExpect(jsonPath("$[0].type.id").value(type.getId().toString()))
                .andExpect(jsonPath("$[0].type.name").value(type.getName()))
                .andExpect(jsonPath("$[0].phoneNumber").value(recyclingPointDto.getPhoneNumber()))
                .andExpect(jsonPath("$[0].address").value(recyclingPointDto.getAddress()))
                .andExpect(jsonPath("$[0].latitude").value(recyclingPointDto.getLatitude()))
                .andExpect(jsonPath("$[0].longitude").value(recyclingPointDto.getLongitude()))
                .andExpect(jsonPath("$[0].categories").isArray());
    }

    @Test
    @DisplayName("POST /recycling-points/by-categories - Получение списка пунктов по категориям - 4xx")
    void getRecyclingPointByCategories_withoutBody_return400() throws Exception {
        mockMvc.perform(post("/api/v1/recycling-points/by-categories"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "adminUser", roles = "ADMIN")
    @DisplayName("POST /recycling-points - Создание пункта - 2xx")
    void createRecyclingPoint_success_return200() throws Exception {
        CreateRecyclingPointRequest request = new CreateRecyclingPointRequest();
        request.setName("test_new_name");
        request.setTypeId(type.getId());
        request.setAddress("test_new_address");
        request.setLatitude(99999.99999);
        request.setLongitude(88888.88888);
        request.setPhoneNumber("+73333333333");
        request.setEmail("new_email@test.test");
        request.setCategoryIds(List.of(UUID.randomUUID()));

        RecyclingPointDto responseDto = new RecyclingPointDto(
                UUID.randomUUID(),
                "test_new_name",
                type,
                "test_new_address",
                99999.99999,
                88888.88888,
                "+73333333333",
                categoryDtoSet
        );

        when(recyclingPointService.createPoint(any()))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/recycling-points")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test_new_name"))
                .andExpect(jsonPath("$.type.id").value(type.getId().toString()))
                .andExpect(jsonPath("$.type.name").value(type.getName()))
                .andExpect(jsonPath("$.address").value("test_new_address"))
                .andExpect(jsonPath("$.latitude").value(99999.99999))
                .andExpect(jsonPath("$.longitude").value(88888.88888))
                .andExpect(jsonPath("$.phoneNumber").value("+73333333333"))
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories[0].id").value(categoryDto.getId().toString()))
                .andExpect(jsonPath("$.categories[0].name").value(categoryDto.getName()));
    }

    @Test
    @WithMockUser(username = "notAdminUser", roles = "USER")
    @DisplayName("POST /recycling-points - Создание пункта - 403")
    void createRecyclingPoint_notAdminUserForbidden_return403() throws Exception {
        CreateRecyclingPointRequest request = new CreateRecyclingPointRequest();

        doThrow(new ForbiddenException("notAdminUser"))
                .when(recyclingPointService.createPoint(any()));

        mockMvc.perform(post("/api/v1/recycling-points")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /recycling-points - Создание пункта - 401")
    void createRecyclingPoint_noUserUnauthorised_return401() throws Exception {
        mockMvc.perform(post("/api/v1/recycling-points")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}