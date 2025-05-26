package com.example.recycling_service.repository;

import com.example.recycling_service.model.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
    List<Advertisement> findAllByOrderByCreatedAtDesc();
    Advertisement save(Advertisement advertisement);

    @Query("SELECT a FROM Advertisement a JOIN a.categories c WHERE c.id IN :categoryIds")
    List<Advertisement> findByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

    List<Advertisement> findByUserId(Long userId);
}
