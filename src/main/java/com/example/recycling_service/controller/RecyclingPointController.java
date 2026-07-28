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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recycling-points")
public class RecyclingPointController {

    @Autowired
    private RecyclingPointService recyclingPointService;

    /**
     * Get all recycling points
     * @return List of recycling points
     */
    @GetMapping
    public List<RecyclingPointDto> getAllPoints() {
        return recyclingPointService.getAllPoints();
    }

    /**
     * Get all categories
     * @return List of categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(recyclingPointService.getAllCategories());
    }

    /**
     * Get recycling point by id
     * @param id id of recycling point
     * @return RecyclingPointDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<RecyclingPointDto> getRecyclingPointById(@PathVariable UUID id) {
        return ResponseEntity.ok(recyclingPointService.getPointById(id));
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
        return ResponseEntity.ok(recyclingPointService.getPointByCategory(request));
    }

    /**
     * Создание точки
     *
     * @param request CreateRecyclingPointRequest
     * @return RecyclingPointDTO
     */
    @PostMapping
    public ResponseEntity<RecyclingPointDto> addPoint(@RequestBody CreateRecyclingPointRequest request) {
        return ResponseEntity.ok(recyclingPointService.createPoint(request));
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
