/*  Data Transfer Objects
    Передача данных между слоями
*/
package com.example.recycling_service.dto;

import com.example.recycling_service.model.RecyclingPoint;
import com.example.recycling_service.model.Type;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecyclingPointDto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private Type type;

    private String address;

    private double latitude;
    private double longitude;

    private String phoneNumber;

    @ManyToMany
    @JoinTable(
            name = "point_category",
            joinColumns = @JoinColumn(name = "recycling_point_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id" , columnDefinition = "bigint")
    )
    private Set<CategoryDto> categories = new HashSet<>();

    public RecyclingPointDto(RecyclingPoint rp) {
        this.name = rp.getName();
        this.type = rp.getType();
        this.phoneNumber = rp.getPhoneNumber();
        this.address = rp.getAddress();
        this.latitude = rp.getLatitude();
        this.longitude = rp.getLongitude();
        this.categories = rp.getCategories() != null ?
                rp.getCategories().stream()
                        .map(CategoryDto::new)
                        .collect(Collectors.toSet()) :
                Collections.emptySet();
    }
}
