package com.example.recycling_service.service;

import com.example.recycling_service.repository.RecyclingPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.recycling_service.model.RecyclingPoint;
import java.util.List;

@Service
public class RecyclingPointService {

    @Autowired
    private RecyclingPointRepository repo;


    /*Находим все точки
    */
    public List<RecyclingPoint> getAllPoints() {
        return repo.findAll();
    }

    public List<RecyclingPoint> findByType(String type) {
        return repo.findByType(type);
    }

    public RecyclingPoint addPoint(RecyclingPoint point) {
        return repo.save(point);
    }
}
