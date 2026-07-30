package com.example.recycling_service.integration;

import com.example.recycling_service.dto.Request.CreateRecyclingPointRequest;
import com.example.recycling_service.dto.Request.RecyclePointFilterRequest;
import com.example.recycling_service.model.Category;
import com.example.recycling_service.model.Type;
import com.example.recycling_service.repository.AdvertisementRepository;
import com.example.recycling_service.repository.CategoryRepository;
import com.example.recycling_service.repository.RecyclingPointRepository;
import com.example.recycling_service.repository.TypeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RecyclingPointIntegrationTest extends BaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RecyclingPointRepository recyclingPointRepository;

    @Autowired
    TypeRepository typeRepository;

    @Autowired
    CategoryRepository categoryRepository;

    private CreateRecyclingPointRequest request;
    private Category category;
    private Type type;
    private static final String DOMEN_URI = "/api/v1/recycling-points";


    @BeforeEach
    void setUp() {
        type = typeRepository.save(new Type("Test Type"));

        category = categoryRepository.save(new Category("Test Category"));

        request = new CreateRecyclingPointRequest();
        request.setName("test_new_name");
        request.setTypeId(type.getId());
        request.setAddress("test_new_address");
        request.setLatitude(99999.99999);
        request.setLongitude(88888.88888);
        request.setPhoneNumber("+73333333333");
        request.setEmail("new_email@test.test");
        request.setCategoryIds(List.of(category.getId()));
    }

    @Test
    @DisplayName("POST /recycling-point - Успешное создание пунктов - 200")
    void createRecyclingPoint_success_return200() throws Exception{
        // Логинимся под администратором
        String jwtToken = loginAsAdminAndGetToken();

        String response = mockMvc.perform(post(DOMEN_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test_new_name"))
                .andExpect(jsonPath("$.type.id").value(type.getId().toString()))
                .andExpect(jsonPath("$.type.name").value(type.getName()))
                .andExpect(jsonPath("$.address").value("test_new_address"))
                .andExpect(jsonPath("$.latitude").value(99999.99999))
                .andExpect(jsonPath("$.longitude").value(88888.88888))
                .andExpect(jsonPath("$.phoneNumber").value("+73333333333"))
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories[0].id").value(category.getId().toString()))
                .andExpect(jsonPath("$.categories[0].name").value("Test Category"))
                .andReturn().getResponse().getContentAsString();

        UUID createdId = UUID.fromString(JsonPath.read(response, "$.id"));
        assertTrue(recyclingPointRepository.existsById(createdId));
    }

    @Test
    @DisplayName("GET /recycling-point - Успешное получение списка пунктов - 200")
    void getAllPoints_success_return200() throws Exception {
        // Логинимся под администратором
        String jwtToken = loginAsAdminAndGetToken();
        
        String response = mockMvc.perform(post(DOMEN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test_new_name"))
                .andExpect(jsonPath("$.type.id").value(type.getId().toString()))
                .andExpect(jsonPath("$.type.name").value(type.getName()))
                .andExpect(jsonPath("$.address").value("test_new_address"))
                .andExpect(jsonPath("$.latitude").value(99999.99999))
                .andExpect(jsonPath("$.longitude").value(88888.88888))
                .andExpect(jsonPath("$.phoneNumber").value("+73333333333"))
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories[0].id").value(category.getId().toString()))
                .andExpect(jsonPath("$.categories[0].name").value("Test Category"))
                .andReturn().getResponse().getContentAsString();

        UUID createdId = UUID.fromString(JsonPath.read(response, "$.id"));
        assertTrue(recyclingPointRepository.existsById(createdId));

        mockMvc.perform(get(DOMEN_URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(createdId.toString()))
                .andExpect(jsonPath("$[0].name").value("test_new_name"))
                .andExpect(jsonPath("$[0].address").value("test_new_address"))
                .andExpect(jsonPath("$[0].latitude").value(99999.99999))
                .andExpect(jsonPath("$[0].longitude").value(88888.88888))
                .andExpect(jsonPath("$[0].phoneNumber").value("+73333333333"))
                .andExpect(jsonPath("$[0].type.id").value(type.getId().toString()))
                .andExpect(jsonPath("$[0].type.name").value("Test Type"))
                .andExpect(jsonPath("$[0].categories").isArray())
                .andExpect(jsonPath("$[0].categories[0].id").value(category.getId().toString()))
                .andExpect(jsonPath("$[0].categories[0].name").value("Test Category"));
    }

    @Test
    @DisplayName("GET /recycling-point/{id} - Успешное получение пункта по ID - 200")
    void getPointById_success_return200() throws Exception {
        // Логинимся под администратором
        String jwtToken = loginAsAdminAndGetToken();
        
        String response = mockMvc.perform(post(DOMEN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test_new_name"))
                .andExpect(jsonPath("$.type.id").value(type.getId().toString()))
                .andExpect(jsonPath("$.type.name").value(type.getName()))
                .andExpect(jsonPath("$.address").value("test_new_address"))
                .andExpect(jsonPath("$.latitude").value(99999.99999))
                .andExpect(jsonPath("$.longitude").value(88888.88888))
                .andExpect(jsonPath("$.phoneNumber").value("+73333333333"))
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories[0].id").value(category.getId().toString()))
                .andExpect(jsonPath("$.categories[0].name").value("Test Category"))
                .andReturn().getResponse().getContentAsString();

        UUID createdId = UUID.fromString(JsonPath.read(response, "$.id"));
        assertTrue(recyclingPointRepository.existsById(createdId));

        mockMvc.perform(get(DOMEN_URI + "/{id}", createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdId.toString()))
                .andExpect(jsonPath("$.name").value("test_new_name"))
                .andExpect(jsonPath("$.address").value("test_new_address"))
                .andExpect(jsonPath("$.latitude").value(99999.99999))
                .andExpect(jsonPath("$.longitude").value(88888.88888))
                .andExpect(jsonPath("$.phoneNumber").value("+73333333333"))
                .andExpect(jsonPath("$.type.id").value(type.getId().toString()))
                .andExpect(jsonPath("$.type.name").value("Test Type"))
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories[0].id").value(category.getId().toString()))
                .andExpect(jsonPath("$.categories[0].name").value("Test Category"));
    }

    @Test
    @DisplayName("GET /recycling-point/categories - Успешное получение списка категорий - 200")
    void getAllCategories_success_return200() throws Exception{
        Category category1 = new Category("Test Category 1");
        Category category2 = new Category("Test Category 2");
        categoryRepository.save(category1);
        categoryRepository.save(category2);

        mockMvc.perform(get(DOMEN_URI+ "/categories"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("[0].name").value("Test Category"))
                .andExpect(jsonPath("[1].name").value("Test Category 1"))
                .andExpect(jsonPath("[2].name").value("Test Category 2"));
    }

    @Test
    @DisplayName("GET /recycling-points/by-categories - Успешное получение списка пунктов по категориям - 200")
    void getAllPointsByCategories_success_return200() throws Exception {
        // Логинимся под администратором
        String jwtToken = loginAsAdminAndGetToken();
        
        Category category1 = new Category("Test Category 1");
        Category category2 = new Category("Test Category 2");
        categoryRepository.save(category1);
        categoryRepository.save(category2);

        String response = mockMvc.perform(post(DOMEN_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test_new_name"))
                .andExpect(jsonPath("$.type.id").value(type.getId().toString()))
                .andExpect(jsonPath("$.type.name").value(type.getName()))
                .andExpect(jsonPath("$.address").value("test_new_address"))
                .andExpect(jsonPath("$.latitude").value(99999.99999))
                .andExpect(jsonPath("$.longitude").value(88888.88888))
                .andExpect(jsonPath("$.phoneNumber").value("+73333333333"))
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories[0].id").value(category.getId().toString()))
                .andExpect(jsonPath("$.categories[0].name").value("Test Category"))
                .andReturn().getResponse().getContentAsString();

        UUID createdId = UUID.fromString(JsonPath.read(response, "$.id"));
        assertTrue(recyclingPointRepository.existsById(createdId));

        RecyclePointFilterRequest filterRequest = new RecyclePointFilterRequest();
        filterRequest.setCategoryIds(List.of(category.getId()));

        mockMvc.perform(post(DOMEN_URI + "/by-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(createdId.toString()))
                .andExpect(jsonPath("$[0].name").value("test_new_name"))
                .andExpect(jsonPath("$[0].address").value("test_new_address"))
                .andExpect(jsonPath("$[0].latitude").value(99999.99999))
                .andExpect(jsonPath("$[0].longitude").value(88888.88888))
                .andExpect(jsonPath("$[0].phoneNumber").value("+73333333333"))
                .andExpect(jsonPath("$[0].type.id").value(type.getId().toString()))
                .andExpect(jsonPath("$[0].type.name").value("Test Type"))
                .andExpect(jsonPath("$[0].categories").isArray())
                .andExpect(jsonPath("$[0].categories[0].id").value(category.getId().toString()))
                .andExpect(jsonPath("$[0].categories[0].name").value("Test Category"));
    }
}
