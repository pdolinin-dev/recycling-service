/*  Модель
    Представляет сущности базы данных
*/
package com.example.recycling_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "recycling_point")
public class RecyclingPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne @JoinColumn(name = "id_type")
    private Type type;
    private String address;

    private double latitude;
    private double longitude;

    private String phone_number;

    @ManyToMany
    @JoinTable(
            name = "point_categories",
            joinColumns = @JoinColumn(name = "id_point"),
            inverseJoinColumns = @JoinColumn(name = "id_category" , columnDefinition = "bigint")
    )
    private Set<Category> categories = new HashSet<>();
}
