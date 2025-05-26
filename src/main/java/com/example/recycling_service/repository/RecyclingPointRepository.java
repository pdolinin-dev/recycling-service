/*  Репозитории
    Отвечает за взаимодействие с базой данных
*/
package com.example.recycling_service.repository;

import com.example.recycling_service.model.RecyclingPoint;
import com.example.recycling_service.model.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecyclingPointRepository extends JpaRepository<RecyclingPoint, Long>{
    //List<RecyclingPoint> findByType(Type type);
    @Query("SELECT rp FROM RecyclingPoint rp LEFT JOIN FETCH rp.type LEFT JOIN FETCH rp.categories")
    List<RecyclingPoint> findAll();
}
