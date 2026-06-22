package com.example.recycling_service.dto;

import com.example.recycling_service.dto.Request.CategoryRequest;
import com.example.recycling_service.model.Advertisement;
import com.example.recycling_service.model.PostImage;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementDTO {
    private UUID id;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    private BigDecimal price;

    private List<String> imageUrls;

    @NotEmpty(message = "At least 1 category required")
    private Set<CategoryRequest> categories;

    @NotEmpty(message = "User cannot be empty")
    private UUID userId;

    private String address;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDateTime createdAt;

    // Конструктор для преобразования Entity -> DTO
    public AdvertisementDTO(Advertisement ad) {
        this.id = ad.getId();
        this.title = ad.getTitle();
        this.description = ad.getDescription();
        this.price = ad.getPrice();
        this.categories = ad.getCategories() != null ?
                ad.getCategories().stream()
                        .map(CategoryRequest::new)
                        .collect(Collectors.toSet()) :
                Collections.emptySet();
        this.userId = ad.getUser().getId();
        this.address = ad.getAddress();
        this.createdAt = ad.getCreatedAt();
        List<PostImage> images = ad.getImages();
        List<String> imageUrls = new ArrayList<>(images.size());
        for (PostImage image : images) {
            imageUrls.add(image.getId().toString());
        }
        this.imageUrls = imageUrls;
    }
}
