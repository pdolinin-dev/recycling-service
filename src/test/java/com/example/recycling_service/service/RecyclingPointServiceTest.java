package com.example.recycling_service.service;

import com.example.recycling_service.dto.RecyclingPointDto;
import com.example.recycling_service.dto.Request.CreateRecyclingPointRequest;
import com.example.recycling_service.dto.Request.RecyclePointFilterRequest;
import com.example.recycling_service.exception.NotFoundException;
import com.example.recycling_service.model.Category;
import com.example.recycling_service.model.RecyclingPoint;
import com.example.recycling_service.model.Type;
import com.example.recycling_service.repository.CategoryRepository;
import com.example.recycling_service.repository.RecyclingPointRepository;
import com.example.recycling_service.repository.TypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecyclingPointServiceTest {

    @Mock
    RecyclingPointRepository recyclingPointRepository;

    @Mock
    TypeRepository typeRepository;

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    RecyclingPointService recyclingPointService;

    @Captor
    ArgumentCaptor<RecyclingPoint> recyclingPointArgumentCaptor;

    private RecyclingPoint recyclingPoint;
    private Category category1;
    private Category category2;


    @BeforeEach
    void setUp() {
        Type type = new Type();
        type.setId(UUID.randomUUID());
        type.setName("test_type_123");

        Set<Category> categorySet = new HashSet<>();
        category1 = new Category();
        category1.setId(UUID.randomUUID());
        category1.setName("test_category_1");
        categorySet.add(category1);
        category2 = new Category();
        category2.setId(UUID.randomUUID());
        category2.setName("test_category_2");
        categorySet.add(category2);

        recyclingPoint = new RecyclingPoint();
        recyclingPoint.setId(UUID.randomUUID());
        recyclingPoint.setName("recycling_point_123");
        recyclingPoint.setType(type);
        recyclingPoint.setAddress("test_address");
        recyclingPoint.setLatitude(123.123);
        recyclingPoint.setLongitude(456.456);
        recyclingPoint.setPhoneNumber("+79999999999");
        recyclingPoint.setEmail("test_email@mail.com");
        recyclingPoint.setCategories(categorySet);
    }

    @Test
    @DisplayName("Получение пункта: по ID")
    void getRecyclingPoint_success() {
        when(recyclingPointRepository.findById(recyclingPoint.getId()))
                .thenReturn(Optional.of(recyclingPoint));

        RecyclingPointDto recyclingPointDTO = recyclingPointService.getPointById(recyclingPoint.getId());

        assertThat(recyclingPointDTO.getId()).isEqualTo(recyclingPoint.getId());
        assertThat(recyclingPointDTO.getName()).isEqualTo(recyclingPoint.getName());
        assertThat(recyclingPointDTO.getType()).isEqualTo(recyclingPoint.getType());
        assertThat(recyclingPointDTO.getAddress()).isEqualTo(recyclingPoint.getAddress());
        assertThat(recyclingPointDTO.getLatitude()).isEqualTo(recyclingPoint.getLatitude());
        assertThat(recyclingPointDTO.getLongitude()).isEqualTo(recyclingPoint.getLongitude());
        assertThat(recyclingPointDTO.getPhoneNumber()).isEqualTo(recyclingPoint.getPhoneNumber());
        assertThat(recyclingPointDTO.getCategories()).hasSize(2);
        assertThat(recyclingPointDTO.getCategories()
                .stream().toList().containsAll(List.of(category1, category2)));
    }

    @Test
    @DisplayName("Ошибка олучения пункта: по ID NotFound")
    void getRecyclingPoint_NotFoundException() {
        when(recyclingPointRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> recyclingPointService.getPointById(UUID.randomUUID()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Получение всех пунктов")
    void getAllPoints_success() {
        when(recyclingPointRepository.findAll())
                .thenReturn(List.of(recyclingPoint));

        List<RecyclingPointDto> recyclingPointDTOList = recyclingPointService.getAllPoints();

        assertThat(recyclingPointDTOList).hasSize(1);
        assertThat(recyclingPointDTOList.getFirst().getId()).isEqualTo(recyclingPoint.getId());
        assertThat(recyclingPointDTOList.getFirst().getName()).isEqualTo(recyclingPoint.getName());
        assertThat(recyclingPointDTOList.getFirst().getType()).isEqualTo(recyclingPoint.getType());
        assertThat(recyclingPointDTOList.getFirst().getAddress()).isEqualTo(recyclingPoint.getAddress());
        assertThat(recyclingPointDTOList.getFirst().getLatitude()).isEqualTo(recyclingPoint.getLatitude());
        assertThat(recyclingPointDTOList.getFirst().getLongitude()).isEqualTo(recyclingPoint.getLongitude());
        assertThat(recyclingPointDTOList.getFirst().getPhoneNumber()).isEqualTo(recyclingPoint.getPhoneNumber());
        assertThat(recyclingPointDTOList.getFirst().getCategories()
                .stream().toList().containsAll(List.of(category1, category2)));
    }

    @Test
    @DisplayName("Создание пункта: успех")
    void createPoint_success() {
        CreateRecyclingPointRequest request = new CreateRecyclingPointRequest();
        request.setName("new_point_123");
        request.setTypeId(UUID.randomUUID());
        request.setAddress("new_point_address_123");
        request.setLatitude(11111.11);
        request.setLongitude(2222.222);
        request.setPhoneNumber("81234567890");
        request.setEmail("new_point_email@gmail.com");
        request.setCategoryIds(List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));

        when(typeRepository.findById(request.getTypeId()))
                .thenReturn(Optional.of(new Type()));

        when(categoryRepository.findAllById(request.getCategoryIds()))
                .thenReturn(List.of(category1, category2));

        when(recyclingPointRepository.save(any()))
                .thenReturn(recyclingPoint);

        RecyclingPointDto recyclingPointDto = recyclingPointService.createPoint(request);

        verify(recyclingPointRepository).save(recyclingPointArgumentCaptor.capture());
        RecyclingPoint newRecyclingPoint = recyclingPointArgumentCaptor.getValue();

        assertThat(newRecyclingPoint.getName()).isEqualTo(request.getName());
        assertThat(newRecyclingPoint.getCategories()).hasSize(2);
        assertThat(newRecyclingPoint.getAddress()).isEqualTo(request.getAddress());
        assertThat(newRecyclingPoint.getLatitude()).isEqualTo(request.getLatitude());
        assertThat(newRecyclingPoint.getLongitude()).isEqualTo(request.getLongitude());
        assertThat(newRecyclingPoint.getPhoneNumber()).isEqualTo(request.getPhoneNumber());
        assertThat(newRecyclingPoint.getEmail()).isEqualTo(request.getEmail());
        assertThat(newRecyclingPoint.getType()).isNotNull();
    }

    @Test
    @DisplayName("Создание пункта: Тип не найден NotFoundException")
    void createPoint_NotFound() {
        CreateRecyclingPointRequest request = new CreateRecyclingPointRequest();

        when(typeRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> recyclingPointService.createPoint(request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Получение пунктов по фильтрам")
    void getPointByCategory_success() {
        RecyclePointFilterRequest request = new RecyclePointFilterRequest();
        request.setCategoryIds(List.of(category1.getId(), category2.getId()));

        when(recyclingPointRepository.findByCategories(request.getCategoryIds()))
                .thenReturn(List.of(recyclingPoint));

        List<RecyclingPointDto> recyclingPointDtoList = recyclingPointService.getPointByCategory(request);

        RecyclingPointDto recyclingPointDto = recyclingPointDtoList.stream().toList().getFirst();
        assertThat(recyclingPointDto.getId()).isEqualTo(recyclingPoint.getId());
        assertThat(recyclingPointDto.getName()).isEqualTo(recyclingPoint.getName());
        assertThat(recyclingPointDto.getType()).isEqualTo(recyclingPoint.getType());
        assertThat(recyclingPointDto.getAddress()).isEqualTo(recyclingPoint.getAddress());
        assertThat(recyclingPointDto.getLatitude()).isEqualTo(recyclingPoint.getLatitude());
        assertThat(recyclingPointDto.getLongitude()).isEqualTo(recyclingPoint.getLongitude());
        assertThat(recyclingPointDto.getPhoneNumber()).isEqualTo(recyclingPoint.getPhoneNumber());
        assertThat(recyclingPointDto.getCategories()
                .stream().toList().containsAll(List.of(category1, category2)));
    }
}