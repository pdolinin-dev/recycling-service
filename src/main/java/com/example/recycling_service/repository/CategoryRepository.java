package com.example.recycling_service.repository;

import com.example.recycling_service.model.Category;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    @Query("SELECT c FROM Category c " +
            "WHERE c.id IN (:categories)" +
            "ORDER BY c.name ASC")
    Set<Category> findAllById(@Param("categories") Set<UUID> categoryIds);

    Category save(Category category);

    long countByIdIn(Set<UUID> categoryIds);

    List<Category> findAll();

    Optional<Category> findById(@NotNull UUID id);

//    Collection<Category> findAllById(@NotEmpty Set<UUID> categoryIds);
}
