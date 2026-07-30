/*  Контроллеры
    Отвечает за обработку http request
*/
package com.example.recycling_service.controller;

import com.example.recycling_service.dto.CategoryDto;
import com.example.recycling_service.dto.PageResponse;
import com.example.recycling_service.dto.RecyclingPointDto;

import com.example.recycling_service.dto.Request.CreateRecyclingPointRequest;
import com.example.recycling_service.dto.Request.RecyclePointFilterRequest;

import com.example.recycling_service.service.RecyclingPointService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/recycling-points")
public class RecyclingPointController {

    @Autowired
    private RecyclingPointService recyclingPointService;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * Get all recycling points
     * @return List of recycling points
     */
    @GetMapping
    public List<RecyclingPointDto> getAllPoints() {
        log.info("Запрос на получение списка пунктов приема");
        List<RecyclingPointDto> response = recyclingPointService.getAllPoints();
        log.info("Получено [{}] пунктов приема", response.size());
        return response;
    }

    /**
     * Get all categories
     * @return List of categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        log.info("Запрос на получение списка категорий");
        List<CategoryDto> response = recyclingPointService.getAllCategories();
        log.info("Получено [{}] категорий", response.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Get recycling point by id
     * @param id id of recycling point
     * @return RecyclingPointDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<RecyclingPointDto> getRecyclingPointById(@PathVariable UUID id) throws JsonProcessingException {
        log.info("Запрос на получение информации по пункту приема с id: [{}]", id);
        RecyclingPointDto response = recyclingPointService.getPointById(id);
        log.info("Получена информация по пункту приема c id: [{}]", response.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Получение точек по категориям
     *
     * @param request RecyclePointFilterRequest
     * @return List<RecyclingPointDTO>
     */
    @PostMapping("/by-categories")
    public ResponseEntity<List<RecyclingPointDto>> getRecyclingPointByCategories(
            @RequestBody RecyclePointFilterRequest request
            ) {
        log.info("Запрос пунктов приема по категориям [{}]", request.getCategoryIds());
        List<RecyclingPointDto> response = recyclingPointService.getPointByCategory(request);
        log.info("Получено {} пунктов приема по категориям: [{}]", response.size(), request.getCategoryIds());
        return ResponseEntity.ok(response);
    }

    /**
     * Создание точки
     *
     * @param request CreateRecyclingPointRequest
     * @return RecyclingPointDTO
     */
    @PostMapping()
    public ResponseEntity<RecyclingPointDto> addPoint(@RequestBody CreateRecyclingPointRequest request) {
        log.warn("Запрос на создание пункта приема [{}]", request.getName());
        RecyclingPointDto response = recyclingPointService.createPoint(request);
        log.info("Создан пункт приема с id: [{}]", response.getId());
        return ResponseEntity.ok(response);
    }

    // Тоже непонятно надо ли
//    @PutMapping("/{id}")
//    public ResponseEntity<RecyclingPointDTO> updatePoint(@PathVariable Long id, @RequestBody RecyclingPointDTO dto) {
//        RecyclingPointDTO updated = recyclingPointService.updatePoint(id, dto);
//        return ResponseEntity.ok(updated);
//    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deletePoint(@PathVariable Long id) {
//        recyclingPointService.deletePoint(id);
//        return ResponseEntity.noContent().build();
//    }



    // Получение точек по фильтру
//    @GetMapping( "/filter")
//    public List<RecyclingPoint> findByType(@RequestParam("type") String type) {
//        //return recyclingPointService.findByType(type);
//    }
}
