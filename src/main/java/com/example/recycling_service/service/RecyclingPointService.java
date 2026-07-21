package com.example.recycling_service.service;

import com.example.recycling_service.dto.CategoryDto;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecyclingPointService {

    @Autowired
    private RecyclingPointRepository recyclingPointRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TypeRepository typeRepository;

    /*Находим все точки
    */
    public List<RecyclingPointDto> getAllPoints() {
        return recyclingPointRepository.findAll()
                .stream().map(this::mapRecyclingPointToDto)
                .toList();
    }

    public RecyclingPoint addPoint(RecyclingPoint point) {
        return recyclingPointRepository.save(point);
    }

    // Get all categories
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream().map(this::mapCategoryToDto)
                .toList();
    }

    // Get point by id
    public RecyclingPointDto getPointById(UUID id) {
        RecyclingPoint recyclingPoint = recyclingPointRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пункт", "id", id));

        return mapRecyclingPointToDto(recyclingPoint);
    }

    public List<RecyclingPointDto> getPointByCategory(RecyclePointFilterRequest request) {
        return recyclingPointRepository.findByCategories(request.getCategoryIds())
                .stream().map(this::mapRecyclingPointToDto)
                .toList();
    }

    public RecyclingPointDto createPoint(CreateRecyclingPointRequest request) {
        RecyclingPoint point = new RecyclingPoint();

        Type type = typeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new NotFoundException("Тип", "id", request.getTypeId()));

        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.getCategoryIds()));

        point.setName(request.getName());
        point.setAddress(request.getAddress());
        point.setLatitude(request.getLatitude());
        point.setLongitude(request.getLongitude());
        point.setPhoneNumber(request.getPhoneNumber());
        point.setEmail(request.getEmail());
        point.setType(type);
        point.setCategories(categories);

        RecyclingPoint savedPoint = recyclingPointRepository.save(point);
        return mapRecyclingPointToDto(savedPoint);
    }

    // HUETA надо переписывать
    @Transactional
    public RecyclingPointDto updatePoint(UUID id, RecyclingPointDto dto) {
        return null;
    }

    @Transactional
    public void deletePoint(UUID id) {
        if (!recyclingPointRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Точка не найдена");
        }
        recyclingPointRepository.deleteById(id);
    }

    private CategoryDto mapCategoryToDto(Category category) {
        CategoryDto dto = new CategoryDto(category);
        dto.setId(category.getId());
        dto.setName(category.getName());

        return dto;
    }

    private RecyclingPointDto mapRecyclingPointToDto(RecyclingPoint point) {
        RecyclingPointDto dto = new RecyclingPointDto(point);
        dto.setId(point.getId());
        dto.setName(point.getName());
        dto.setAddress(point.getAddress());
        dto.setType(point.getType());
        dto.setLatitude(point.getLatitude());
        dto.setLongitude(point.getLongitude());
        dto.setPhoneNumber(point.getPhoneNumber());
        dto.setCategories(point.getCategories() != null ?
                point.getCategories().stream()
                        .map(this::mapCategoryToDto)
                        .collect(Collectors.toSet()) :
                null);
        return dto;
    }
}
