package com.example.recycling_service.service;

import  com.example.recycling_service.dto.AdvertisementDTO;
import com.example.recycling_service.dto.RecyclingPointDTO;
import com.example.recycling_service.model.Advertisement;
import com.example.recycling_service.model.Category;
import com.example.recycling_service.model.RecyclingPoint;
import com.example.recycling_service.model.Type;
import com.example.recycling_service.repository.CategoryRepository;
import com.example.recycling_service.repository.RecyclingPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RecyclingPointService {

    @Autowired
    private RecyclingPointRepository recyclingPointRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    /*Находим все точки
    */
    public List<RecyclingPoint> getAllPoints() {
        return recyclingPointRepository.findAll();
    }

//    public List<RecyclingPoint> findByType(Type type) {
//        return recyclingPointRepository.findByType(type);
//    }

    public RecyclingPoint addPoint(RecyclingPoint point) {
        return recyclingPointRepository.save(point);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public RecyclingPointDTO getPointById(Long id) {
        RecyclingPoint recyclingPoint = recyclingPointRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Point not found with id: " + id
                ));
        return new RecyclingPointDTO(recyclingPoint);
    }
}
