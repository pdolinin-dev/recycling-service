package com.example.recycling_service.repository;

import com.example.recycling_service.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {


    @Query("SELECT p FROM Post p WHERE p.id = :id")
    Optional<Post> findPostById(@Param("id") UUID id);


    // Пример метода с JOIN для изображений
//    @Query("SELECT p FROM Post p WHERE p.id = :id")
//    Optional<Post> findByIdWithImages(@Param("id") Long id);

    @Query("SELECT p FROM Post p WHERE " +
            "(cast(:startDate as timestamp) IS NULL OR p.createdAt >= :startDate) AND " +
            "(cast(:endDate as timestamp) IS NULL OR p.createdAt <= :endDate) " +
            "ORDER BY p.createdAt DESC")
    List<Post> findFilteredPosts(
            @Param("type") String type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
