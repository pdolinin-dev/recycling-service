package com.example.recycling_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import com.example.recycling_service.model.Media;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@Table(name = "advertisement")
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Title cannot be blank")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "description can not be blank")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private String address;

    @ManyToMany
    @JoinTable(
            name = "advertisement_category",
            joinColumns = @JoinColumn(name = "advertisement_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @NotEmpty(message = "At least 1 category")
    private Set<Category> categories = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "advertisement_media",
            joinColumns = @JoinColumn(name = "advertisement_id"),
            inverseJoinColumns = @JoinColumn(name = "media_id")
    )
    private List<Media> media = new ArrayList<>();
}