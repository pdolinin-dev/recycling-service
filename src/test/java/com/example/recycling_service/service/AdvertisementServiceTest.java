package com.example.recycling_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.recycling_service.dto.PageResponse;
import com.example.recycling_service.dto.Request.CreateAdvertisementRequest;
import com.example.recycling_service.dto.Request.FilterAdvertisementRequest;
import com.example.recycling_service.dto.Request.UpdateAdvertisementRequest;
import com.example.recycling_service.exception.ForbiddenException;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class AdvertisementServiceTest {

    @Mock
    AdvertisementRepository advertisementRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Captor
    ArgumentCaptor<Advertisement> advertisementArgumentCaptor;

    @InjectMocks
    AdvertisementService advertisementService;

    private AdvertisementResponse advertisementResponse;
    private Advertisement advertisement;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setLogin("test_login_123");
        user.setPassword("test_passwordhash_123");
        user.setEmail("test_email_123");
        user.setName("test_name_123");
        user.setRole(Role.USER);

//        advertisementResponse = new AdvertisementResponse();
//        advertisementResponse.setId(UUID.randomUUID());
//        advertisementResponse.setTitle("test_title_123");
//        advertisementResponse.setCategories(new HashSet<>());
//        advertisementResponse.setPrice(BigDecimal.valueOf(123345));
//        advertisementResponse.setAddress("test_advertisement_address_123");
//        advertisementResponse.setDescription("test_description_123");
//        advertisementResponse.setUserId(user.getId());

        advertisement = new Advertisement();
        advertisement.setId(UUID.randomUUID());
        advertisement.setTitle("test_title_123");
        advertisement.setUser(user);
        advertisement.setCategories(new HashSet<>());
        advertisement.setPrice(BigDecimal.valueOf(12345));
        advertisement.setAddress("test_advertisement_address_123");
        advertisement.setDescription("test_description_123");
    }

    @Test
    @DisplayName("Получение объявлений: по ID")
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
    @DisplayName("Успешно создаем объявление")
    void createAdvertisement_success() {

        CreateAdvertisementRequest request = new CreateAdvertisementRequest();
        Set<UUID> categoryIds = new HashSet<>();
        categoryIds.add(UUID.randomUUID());
        request.setCategoryIds(categoryIds);
        request.setDescription("test_description_123");
        request.setTitle("test_title_123");
        request.setPrice(BigDecimal.valueOf(12345));

        Set<Category> categorySet = new HashSet<>();
        Category category1 = new Category();
        categorySet.add(category1);

        when(userRepository.findByLogin("test_login_123"))
                .thenReturn(Optional.of(user));

        when(categoryRepository.findAllById(any()))
                .thenReturn(categorySet);

        when(advertisementRepository.save(any()))
                .thenReturn(advertisement);

        AdvertisementResponse saved = advertisementService.createAdvertisement(request, "test_login_123");

        verify(advertisementRepository).save(advertisementArgumentCaptor.capture());
        Advertisement advertisementValue = advertisementArgumentCaptor.getValue();

        assertThat(advertisementValue.getPrice()).isEqualTo(request.getPrice());
        assertThat(saved.getUserId()).isEqualTo(user.getId());
        assertThat(advertisementValue.getTitle()).isEqualTo(request.getTitle());
        assertThat(advertisementValue.getDescription()).isEqualTo(request.getDescription());
    }

    @Test
    @DisplayName("Получение объявления: объявление с ID не найдено NotFoundException")
    void getAdvertisementById_NotFoundException() {
        UUID advertisementId = UUID.randomUUID();

        when(advertisementRepository.findById(advertisementId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> advertisementService.findAdvertisementById(advertisementId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Успешное обновление объявления")
    void updateAdvertisement_success() {
        UpdateAdvertisementRequest request = new UpdateAdvertisementRequest();
        Set<UUID> categoryIds = new HashSet<>();
        categoryIds.add(UUID.randomUUID());
        categoryIds.add(UUID.randomUUID());
        request.setCategoryIds(categoryIds);
        request.setDescription("changed_test_description_123");
        request.setTitle("changed_test_title_123");
        request.setPrice(BigDecimal.valueOf(100));

        Set<Category> categorySet = new HashSet<>();
        Category category1 = new Category();
        Category category2 = new Category();
        categorySet.add(category1);
        categorySet.add(category2);


        when(advertisementRepository.findById(advertisement.getId()))
                .thenReturn(Optional.of(advertisement));

        when(userRepository.findByLogin(user.getLogin()))
                .thenReturn(Optional.of(user));

       when(categoryRepository.findAllById(request.getCategoryIds()))
                .thenReturn(categorySet);

        when(advertisementRepository.save(any()))
                .thenReturn(advertisement);


        AdvertisementResponse updated = advertisementService.updateAdvertisement(advertisement.getId(), request, user.getLogin());

        verify(advertisementRepository).save(advertisementArgumentCaptor.capture());
        Advertisement advertisementValue = advertisementArgumentCaptor.getValue();

        assertThat(advertisementValue.getPrice()).isEqualTo(request.getPrice());
        assertThat(advertisementValue.getTitle()).isEqualTo(request.getTitle());
        assertThat(advertisementValue.getDescription()).isEqualTo(request.getDescription());
    }

    @Test
    @DisplayName("Обновление объявления: объявление с ID не найдено NotFoundException")
    void updateAdvertisement_NotFoundException_Advertisement() {
        UUID advertisementId = UUID.randomUUID();
        UpdateAdvertisementRequest request = new UpdateAdvertisementRequest();

        when(advertisementRepository.findById(advertisementId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> advertisementService.updateAdvertisement(advertisementId, request, user.getLogin()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Обновление объявления: пользователь с ID не найден NotFoundException")
    void updateAdvertisement_NotFoundException_User() {
        UUID advertisementId = UUID.randomUUID();
        UpdateAdvertisementRequest request = new UpdateAdvertisementRequest();

        when(advertisementRepository.findById(advertisementId))
                .thenReturn(Optional.of(advertisement));

        when(userRepository.findByLogin(user.getLogin()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> advertisementService.updateAdvertisement(advertisementId, request, user.getLogin()))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    @DisplayName("Обновление объявления: недостаточно прав для удаления")
    void updateAdvertisement_ForbiddenException() {
        User fakeUser = new User();
        fakeUser.setId(UUID.randomUUID());
        fakeUser.setRole(Role.USER);
        fakeUser.setLogin("fake_login_123");
        fakeUser.setPassword("fake_passwordhash_123");
        fakeUser.setEmail("fake_email_123");
        fakeUser.setName("fake_name_123");

        UpdateAdvertisementRequest request = new UpdateAdvertisementRequest();

        when(userRepository.findByLogin(any()))
                .thenReturn(Optional.of(fakeUser));

        when(advertisementRepository.findById(any()))
                .thenReturn(Optional.of(advertisement));

        assertThatThrownBy(() -> advertisementService.updateAdvertisement(advertisement.getId(), request, fakeUser.getLogin()))
                .isInstanceOf(ForbiddenException.class);
    }


    @Test
    @DisplayName("Удаление объявления: успех для владельца объявления")
    void deleteAdvertisement_success_advertisementOwner() {
        when(userRepository.findByLogin(user.getLogin()))
                .thenReturn(Optional.of(user));

        when(advertisementRepository.findById(advertisement.getId()))
                .thenReturn(Optional.of(advertisement));

        advertisementService.deleteAdvertisement(advertisement.getId(), user.getLogin());

        verify(advertisementRepository).delete(advertisement);
    }

    @Test
    @DisplayName("Удаление объявления: успех для админа")
    void deleteAdvertisement_success_admin() {
        User admin = new User();
        admin.setId(UUID.randomUUID());
        admin.setRole(Role.ADMIN);
        admin.setLogin("test_admin_login_123");
        admin.setPassword("test_admin_passwordhash_123");
        admin.setEmail("test_admin_email_123");
        admin.setName("test_admin_name_123");

        when(userRepository.findByLogin(admin.getLogin()))
                .thenReturn(Optional.of(admin));

        when(advertisementRepository.findById(advertisement.getId()))
                .thenReturn(Optional.of(advertisement));

        advertisementService.deleteAdvertisement(advertisement.getId(), admin.getLogin());

        verify(advertisementRepository).delete(advertisement);
    }

    @Test
    @DisplayName("Удаление объявления: пользователь с ID не найден NotFoundException")
    void deleteAdvertisement_NotFoundException_User() {
        UUID advertisementId = UUID.randomUUID();

        when(userRepository.findByLogin(user.getLogin()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> advertisementService.deleteAdvertisement(advertisementId, user.getLogin()))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    @DisplayName("Удаление объявления: объявление с ID не найдено NotFoundException")
    void deleteAdvertisement_NotFoundException_Advertisement() {
        UUID advertisementId = UUID.randomUUID();

        when(userRepository.findByLogin(user.getLogin()))
                .thenReturn(Optional.of(user));

        when(advertisementRepository.findById(advertisementId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> advertisementService.deleteAdvertisement(advertisementId, user.getLogin()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Удаление объявления: недостаточно прав для удаления")
    void deleteAdvertisement_Forbidden() {
        User fakeUser = new User();
        fakeUser.setId(UUID.randomUUID());
        fakeUser.setRole(Role.USER);
        user.setLogin("fake_login_123");
        user.setPassword("fake_passwordhash_123");
        user.setEmail("fake_email_123");
        user.setName("fake_name_123");

        when(userRepository.findByLogin(any()))
                .thenReturn(Optional.of(fakeUser));

        when(advertisementRepository.findById(advertisement.getId()))
                .thenReturn(Optional.of(advertisement));

        assertThatThrownBy(() -> advertisementService.deleteAdvertisement(advertisement.getId(), fakeUser.getLogin()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("Получение объявлений: по категориям")
    void getAdvertisement_success_categories() {
        FilterAdvertisementRequest request = new FilterAdvertisementRequest();
        request.setCategoryIds(List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        List<Advertisement> advertisementList = List.of(advertisement);

        int pageNumber = 1;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageSize, pageNumber, Sort.by("createdAt").descending());
        Page<Advertisement> page =  new PageImpl<>(advertisementList, pageable, advertisementList.size());

        when(advertisementRepository.findByCategoryIds(request.getCategoryIds(), pageable))
                .thenReturn(page);

        PageResponse<AdvertisementResponse> response = advertisementService.findByCategoryIds(request, pageNumber, pageSize);
        assertThat(response.getContent()).hasSize(1);

        AdvertisementResponse advertisementValue = response.getContent().getFirst();
        assertThat(advertisementValue.getPrice()).isEqualTo(advertisement.getPrice());
        assertThat(advertisementValue.getTitle()).isEqualTo(advertisement.getTitle());
        assertThat(advertisementValue.getDescription()).isEqualTo(advertisement.getDescription());

        verify(advertisementRepository).findByCategoryIds(request.getCategoryIds(), pageable);
    }
}