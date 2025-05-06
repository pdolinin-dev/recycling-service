package com.example.recycling_service.repository;

import com.example.recycling_service.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Set<Category> findAllByIdIn(Set<Long> categoryIds);
    long countByIdIn(Set<Long> categoryIds);
    List<Category> findAll();
}
