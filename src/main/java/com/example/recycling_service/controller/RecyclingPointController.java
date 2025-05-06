/*  Контроллеры
    Отвечает за обработку http request
*/
package com.example.recycling_service.controller;

import com.example.recycling_service.model.RecyclingPoint;
import com.example.recycling_service.service.RecyclingPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // Получение точек по фильтру
    @GetMapping( "/filter")
    public List<RecyclingPoint> findByType(@RequestParam("type") String type) {
        return recyclingPointService.findByType(type);
    }

    // Добавление точки в базу
    @PostMapping
    public RecyclingPoint addPoint(@RequestBody RecyclingPoint point) {
        return recyclingPointService.addPoint(point);
    }
}
