package com.example.recycling_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "media")
@Getter
@Setter
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
    private String mimeType;

    @PositiveOrZero
    @Column(name = "byte_size")
    private int size;

    @NotBlank
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @NotBlank
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
