package com.example.recycling_service.repository;

import com.example.recycling_service.model.Category;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Set<Category> findAllByIdIn(Set<UUID> categoryIds);

    long countByIdIn(Set<UUID> categoryIds);

    List<Category> findAll();

    Optional<Category> findById(UUID id);

    Collection<Category> findAllById(@NotEmpty Set<UUID> categoryIds);
}
