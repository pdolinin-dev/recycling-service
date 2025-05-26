package com.example.recycling_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "advertisement_categories")
public class AdvertisementCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "advertisement_id")
    private Long advertisementId;

    @Column(name = "category_id", columnDefinition = "bigint")
    private Long categoryId;

    // Геттеры и сеттеры
}
