package com.example.recycling_service.repository;

import com.example.recycling_service.model.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TypeRepository extends JpaRepository<Type, UUID> {
    Optional<Type> findById(UUID id);
}
