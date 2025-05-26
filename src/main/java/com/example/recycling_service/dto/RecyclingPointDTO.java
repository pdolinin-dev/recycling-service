/*  Data Transfer Objects
    Передача данных между слоями
*/
package com.example.recycling_service.dto;

import com.example.recycling_service.model.Advertisement;
import com.example.recycling_service.model.AdvertisementImage;
import com.example.recycling_service.model.Category;
import com.example.recycling_service.model.RecyclingPoint;
import com.example.recycling_service.model.Type;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecyclingPointDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

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
    private Set<CategoryRequest> categories = new HashSet<>();

    public RecyclingPointDTO(RecyclingPoint rp) {
        this.id = rp.getId();
        this.name = rp.getName();
        this.type = rp.getType();
        this.phone_number = rp.getPhone_number();
        this.address = rp.getAddress();
        this.latitude = rp.getLatitude();
        this.longitude = rp.getLongitude();
        this.categories = rp.getCategories() != null ?
                rp.getCategories().stream()
                        .map(CategoryRequest::new)
                        .collect(Collectors.toSet()) :
                Collections.emptySet();
    }
}
