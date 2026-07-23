package com.example.recycling_service.controller;

import com.example.recycling_service.dto.PageResponse;
import com.example.recycling_service.dto.Request.FilterAdvertisementRequest;
import com.example.recycling_service.dto.Response.AdvertisementResponse;
import com.example.recycling_service.dto.CategoryDto;
import com.example.recycling_service.dto.Request.CreateAdvertisementRequest;
import com.example.recycling_service.dto.Request.UpdateAdvertisementRequest;
import com.example.recycling_service.model.Category;
import com.example.recycling_service.service.AdvertisementService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/advertisements")
@RequiredArgsConstructor
@Validated
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    /**
     * Get advertisements by categories
     * @param request Request for filter advertisement
     * @return List of advertisements
     */
    @PostMapping("/by-categories")
    public ResponseEntity<PageResponse<AdvertisementResponse>> getByCategories(
            @Valid @RequestBody FilterAdvertisementRequest request,
            @RequestParam(defaultValue = "1") int pageSize,
            @RequestParam(defaultValue = "20") int pageNumber) {
        log.info("Запрос объявлений по категориям {}", request.getCategoryIds());
        PageResponse<AdvertisementResponse> result = advertisementService.findByCategoryIds(request, pageSize, pageNumber);
        log.info("Найдено объявлений {}", result.getTotalElements());
        return ResponseEntity.ok(result);
    }

    /**
     * Delete advertisement
     * @param id Advertisement id
     * @param authentication Authentication
     * @return Void
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdvertisement(
            @PathVariable UUID id,
            Authentication authentication) {
        log.info("Запрос на удаление объявления [{}], пользователь: [{}]", id, authentication.getName());
        advertisementService.deleteAdvertisement(id, authentication.getName());
        log.info("Объявление [{}] удалено", id);
        return ResponseEntity.noContent().build();
    }

    //    @Deprecated
//    @PostMapping("/{id}/images")
//    public ResponseEntity<AdvertisementResponse> uploadAdImage(
//            @PathVariable UUID id,
//            @RequestParam("file") MultipartFile file
//    ) throws IOException {
//        return ResponseEntity.ok(advertisementService.addImage(id, file));
//    }

    /**
     * Update advertisement
     * @param id Advertisement id
     * @param authentication Authentication
     * @param request AdvertisementRequest
     * @return AdvertisementResponse
     */
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdvertisementResponse> updateAdvertisement(@PathVariable UUID id,
                                                                     @Valid @RequestBody UpdateAdvertisementRequest request,
                                                                    Authentication authentication) {
        log.info("Запрос на обновления объявления [{}], Пользователь: [{}]", id, authentication.getName());
        AdvertisementResponse response = advertisementService.updateAdvertisement(id, request, authentication.getName());
        log.info("Объявление [{}] обновлено", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all advertisements
     * @return List<AdvertisementResponse>
     */
    @GetMapping
    public ResponseEntity<PageResponse<AdvertisementResponse>> getAllAdvertisements(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize) {
        log.info("Запрос всех объявлений");
        PageResponse<AdvertisementResponse> result = advertisementService.findAll(pageSize, pageNumber);
        log.info("Всего объявлений: {}, показано: {}", result.getTotalElements(), pageSize);
        return ResponseEntity.ok(result);
    }

    /**
     *
     * @param request AdvertisementRequest
     * @param authentication Authentication
     * @return AdvertisementResponse
     * @throws IOException
     */
    @PostMapping
    public ResponseEntity<AdvertisementResponse> createAdvertisement(
            @RequestBody @Valid CreateAdvertisementRequest request,
            Authentication authentication) {
        log.info("Запрос на создание объявления: title=[{}], Пользователь: [{}]",
                request.getTitle(), authentication.getName());
        AdvertisementResponse response = advertisementService.createAdvertisement(
                request, authentication.getName());
        log.info("Объявление создано с id [{}]", response.getId());
        return ResponseEntity.ok(response);
    }

    /**
     *
     * @param id Advertisement id
     * @return Advertisement
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdvertisementResponse> getAdvertisementById(@PathVariable UUID id) {
        log.debug("Запрос объявления по id [{}]", id);
        return ResponseEntity.ok(advertisementService.findAdvertisementById(id));
    }

    /**
     * Получение всех категорий
     * @return Список всех категорий
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        log.debug("Запрос всех категорий");
        List<Category> categories = advertisementService.getAllCategories();
        List<CategoryDto> response = categories.stream().map(CategoryDto::new).collect(Collectors.toList());
        log.debug("Возвращено категорий: {}", response.size());
        return ResponseEntity.ok(response);
    }
}
