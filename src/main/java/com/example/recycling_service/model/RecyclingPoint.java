/*  Модель
    Представляет сущности базы данных
*/
package com.example.recycling_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "recycling_point")
public class RecyclingPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private Type type;

    private String address;

    private double latitude;
    private double longitude;

    private String phoneNumber;

    private String email;

    @ManyToMany
    @JoinTable(
            name = "recycling_point_category",
            joinColumns = @JoinColumn(name = "recycling_point_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories;
}
