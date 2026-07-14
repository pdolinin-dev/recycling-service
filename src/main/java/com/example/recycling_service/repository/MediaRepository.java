package com.example.recycling_service.repository;

import com.example.recycling_service.model.Media;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MediaRepository extends JpaRepository<Media, UUID> {

    Media save(Media media);

}
