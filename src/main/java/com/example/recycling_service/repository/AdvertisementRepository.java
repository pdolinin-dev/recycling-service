package com.example.recycling_service.repository;

import com.example.recycling_service.model.Advertisement;

import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdvertisementRepository extends JpaRepository<Advertisement, UUID> {
    List<Advertisement> findAllByOrderByCreatedAtDesc();

    Advertisement save(Advertisement advertisement);

    @Query("SELECT a FROM Advertisement a JOIN a.categories c WHERE c.id IN :categoryIds")
    List<Advertisement> findByCategoryIds(@Param("categoryIds") List<UUID> categoryIds);

    @Query("SELECT a FROM Advertisement a JOIN a.categories c WHERE c.id IN :categoryIds")
    Page<Advertisement> findByCategoryIds(@Param("categoryIds") List<UUID> categoryIds, Pageable pageable);

    List<Advertisement> findByUserId(UUID userId);

    Optional<Advertisement> findById(UUID id);
}
