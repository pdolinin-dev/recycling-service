package com.example.recycling_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.UUID;

@Entity
@Table(name = "media")
@Getter
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(name = "file_path")
    private String filePath;

    @NotBlank
    private String name;

    @NotBlank
    private String mime_type;

    @NotBlank
    @Column(name = "byte_size")
    private int size;

    @NotBlank
    @Column(name = "created_at")
    private String createdAt;

    @NotBlank
    @Column(name = "updated_at")
    private String updatedAt;
}
