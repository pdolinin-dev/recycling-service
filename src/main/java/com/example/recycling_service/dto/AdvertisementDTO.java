package com.example.recycling_service.dto;

import com.example.recycling_service.model.Advertisement;
import com.example.recycling_service.model.AdvertisementImage;
import com.example.recycling_service.model.Category;
import com.example.recycling_service.model.User;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementDTO {
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    private BigDecimal price;

    private List<String> imageUrls;

    @NotEmpty(message = "At least 1 category required")
    private Set<CategoryRequest> categories;

    @NotEmpty(message = "User cannot be empty")
    private Long userId;

    // Конструктор для преобразования Entity -> DTO
    public AdvertisementDTO(Advertisement ad) {
        this.id = ad.getId();
        this.title = ad.getTitle();
        this.description = ad.getDescription();
        this.price = ad.getPrice();
        this.imageUrls = ad.getImages() != null ?
                ad.getImages().stream()
                        .map(AdvertisementImage::getImageUrl)
                        .collect(Collectors.toList()) :
                Collections.emptyList();
        this.categories = ad.getCategories() != null ?
                ad.getCategories().stream()
                        .map(CategoryRequest::new)
                        .collect(Collectors.toSet()) :
                Collections.emptySet();
        this.userId = ad.getUser().getId();
    }
}
