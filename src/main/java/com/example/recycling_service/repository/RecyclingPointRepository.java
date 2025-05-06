/*  Репозитории
    Отвечает за взаимодействие с базой данных
*/
package com.example.recycling_service.repository;

import com.example.recycling_service.model.RecyclingPoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecyclingPointRepository extends JpaRepository<RecyclingPoint, Long>{
    List<RecyclingPoint> findByType(String type);
    List<RecyclingPoint> findAll();
}
