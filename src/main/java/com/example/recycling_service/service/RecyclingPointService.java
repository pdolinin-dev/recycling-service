package com.example.recycling_service.service;

import  com.example.recycling_service.dto.AdvertisementDTO;
import com.example.recycling_service.dto.RecyclingPointDTO;
import com.example.recycling_service.model.Advertisement;
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

import java.util.List;
import java.util.Set;
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

    @Transactional
    public RecyclingPointDTO updatePoint(Long id, RecyclingPointDTO dto) {
        RecyclingPoint point = recyclingPointRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Точка не найдена"));

        point.setName(dto.getName());
        point.setAddress(dto.getAddress());
        point.setLatitude(dto.getLatitude());
        point.setLongitude(dto.getLongitude());
        point.setPhone_number(dto.getPhone_number());

        // Обновляем тип
        if (dto.getType() != null && dto.getType().getId() != null) {
            point.setType(typeRepository.findById(dto.getType().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Тип не найден")));
        }

        // Обновляем категории
        if (dto.getCategories() != null) {
            Set<Category> categories = dto.getCategories().stream()
                    .map(catDto -> categoryRepository.findById(catDto.getId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Категория не найдена: id " + catDto.getId())))
                    .collect(Collectors.toSet());
            point.setCategories(categories);
        }

        RecyclingPoint updated = recyclingPointRepository.save(point);
        return new RecyclingPointDTO(updated);
    }

    @Transactional
    public void deletePoint(Long id) {
        if (!recyclingPointRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Точка не найдена");
        }
        recyclingPointRepository.deleteById(id);
    }
}
