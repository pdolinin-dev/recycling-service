package com.example.recycling_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.recycling_service.dto.Request.CreateAdvertisementRequest;
import com.example.recycling_service.dto.Request.LoginRequest;
import com.example.recycling_service.dto.Request.RegisterRequest;
import com.example.recycling_service.exception.NotFoundException;
import com.example.recycling_service.model.Advertisement;
import com.example.recycling_service.model.Category;
import com.example.recycling_service.model.Enum.Role;
import com.example.recycling_service.model.User;
import com.example.recycling_service.repository.AdvertisementRepository;

import com.example.recycling_service.dto.Response.AdvertisementResponse;

import com.example.recycling_service.repository.CategoryRepository;
import com.example.recycling_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class AdvertisementServiceTest {

    @Mock
    AdvertisementRepository advertisementRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    AdvertisementService advertisementService;

    private AdvertisementResponse advertisementResponse;
    private Advertisement advertisement;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setLogin("test_login_123");
        user.setPassword("test_passwordhash_123");
        user.setEmail("test_email_123");
        user.setName("test_name_123");
        user.setRole(Role.USER);

        advertisementResponse = new AdvertisementResponse();
        advertisementResponse.setId(UUID.randomUUID());
        advertisementResponse.setTitle("test_title_123");
        advertisementResponse.setCategories(new HashSet<>());
        advertisementResponse.setPrice(BigDecimal.valueOf(123345));
        advertisementResponse.setAddress("test_advertisement_address_123");
        advertisementResponse.setDescription("test_description_123");
        advertisementResponse.setUserId(user.getId());

        advertisement = new Advertisement();
        advertisement.setId(UUID.randomUUID());
        advertisement.setTitle("test_title_123");
        advertisement.setCategories(new HashSet<>());
        advertisement.setPrice(BigDecimal.valueOf(12345));
        advertisement.setAddress("test_advertisement_address_123");
        advertisement.setDescription("test_description_123");
        advertisement.setUser(user);
    }

    @Test
    @DisplayName("Успешно получаем объявление по его ID")
    void getAdvertisement_success() {
        when(advertisementRepository.findById(advertisement.getId()))
                .thenReturn(Optional.of(advertisement));

        advertisementResponse = advertisementService.findAdvertisementById(advertisement.getId());

        assertThat(advertisementResponse.getTitle()).isEqualTo("test_title_123");
        assertThat(advertisementResponse.getCategories()).isEqualTo(new HashSet<>());
        assertThat(advertisementResponse.getPrice()).isEqualTo(BigDecimal.valueOf(12345));
        assertThat(advertisementResponse.getAddress()).isEqualTo("test_advertisement_address_123");
        assertThat(advertisementResponse.getDescription()).isEqualTo("test_description_123");
    }

    @Test
    void createAdvertisement_success() {

        CreateAdvertisementRequest request = new CreateAdvertisementRequest();
        Set<UUID> categoryIds = new HashSet<>();
        categoryIds.add(UUID.randomUUID());
        categoryIds.add(UUID.randomUUID());
        request.setCategoryIds(categoryIds);
        request.setDescription("test_description_123");
        request.setTitle("test_title_123");
        request.setPrice(BigDecimal.valueOf(12345));

        Set<Category> categorySet = new HashSet<>();
        Category category1 = new Category();
        Category category2 = new Category();
        categorySet.add(category1);
        categorySet.add(category2);

        when(userRepository.findByUsername("test_login_123"))
                .thenReturn(Optional.of(user));

        when(categoryRepository.findAllById(request.getCategoryIds()))
                .thenReturn(categorySet);

        when(advertisementRepository.save(any()))
                .thenReturn(advertisement);

        AdvertisementResponse saved = advertisementService.createAdvertisement(request, "test_login_123");

        assertThat(saved.getPrice()).isEqualTo(request.getPrice());
        assertThat(saved.getUserId()).isEqualTo(user.getId());
        assertThat(saved.getTitle()).isEqualTo(request.getTitle());
        assertThat(saved.getDescription()).isEqualTo(request.getDescription());
    }

    @Test
    @DisplayName("Объявление с id не найдено: вызвался NotFoundException")
    void getAdvertisementById_NotFoundException() {
        UUID advertisementId = UUID.randomUUID();

        when(advertisementRepository.findById(advertisementId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> advertisementService.findAdvertisementById(advertisementId))
                .isInstanceOf(NotFoundException.class);
    }
}