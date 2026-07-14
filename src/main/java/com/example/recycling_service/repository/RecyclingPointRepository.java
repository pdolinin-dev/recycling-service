/*  Репозитории
    Отвечает за взаимодействие с базой данных
*/
package com.example.recycling_service.repository;

import com.example.recycling_service.model.Category;
import com.example.recycling_service.model.RecyclingPoint;
import com.example.recycling_service.model.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecyclingPointRepository extends JpaRepository<RecyclingPoint, UUID>{

    @Query("SELECT rp FROM RecyclingPoint rp JOIN rp.categories c " +
            "WHERE c.id IN :categoryIds")
    List<RecyclingPoint> findByCategories(@Param("categoryIds") List<UUID> categories);

    List<RecyclingPoint> findAll();

    Optional<RecyclingPoint> findById(UUID id);
}
