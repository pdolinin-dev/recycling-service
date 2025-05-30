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

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Кастомные методы при необходимости
    List<Post> findByAuthor(String author);
    List<Post> findByCreatedAtAfter(LocalDateTime date);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Post> findPostById(Long id);
    // Пример метода с JOIN для изображений
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Post> findByIdWithImages(@Param("id") Long id);

    @Query("SELECT p FROM Post p WHERE " +
            "(:type IS NULL OR p.type = :type) AND " +
            "(cast(:startDate as timestamp) IS NULL OR p.createdAt >= :startDate) AND " +
            "(cast(:endDate as timestamp) IS NULL OR p.createdAt <= :endDate) " +
            "ORDER BY p.createdAt DESC")
    List<Post> findFilteredPosts(
            @Param("type") String type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
