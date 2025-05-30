/*  Контроллеры
    Отвечает за обработку http request
*/
package com.example.recycling_service.controller;

import com.example.recycling_service.dto.AdvertisementDTO;
import com.example.recycling_service.dto.CategoryRequest;
import com.example.recycling_service.dto.RecyclingPointDTO;
import com.example.recycling_service.model.Category;
import com.example.recycling_service.model.RecyclingPoint;
import com.example.recycling_service.service.RecyclingPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/recycling-points")

public class RecyclingPointController {

    @Autowired
    private RecyclingPointService recyclingPointService;

    // Получение всех точек
    @GetMapping
    public List<RecyclingPoint> getAllPoints() {
        return recyclingPointService.getAllPoints();
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryRequest>> getAllCategories() {
        List<Category> categories = recyclingPointService.getAllCategories();
        List<CategoryRequest> requests = categories.stream().map(CategoryRequest::new).collect(Collectors.toList());
        return ResponseEntity.ok(requests);
    }

    // Get current point details by id
    @GetMapping("/{id}")
    public ResponseEntity<RecyclingPointDTO> getRecyclingPointById(@PathVariable Long id) {
        RecyclingPointDTO recyclingPointDTO = recyclingPointService.getPointById(id);
        return ResponseEntity.ok(recyclingPointDTO);
    }

    //Добавление точки в базу
    @PostMapping
    public RecyclingPoint addPoint(@RequestBody RecyclingPoint point) {
        return recyclingPointService.addPoint(point);
    }

    // Получение точек по фильтру
//    @GetMapping( "/filter")
//    public List<RecyclingPoint> findByType(@RequestParam("type") String type) {
//        //return recyclingPointService.findByType(type);
//    }
}
