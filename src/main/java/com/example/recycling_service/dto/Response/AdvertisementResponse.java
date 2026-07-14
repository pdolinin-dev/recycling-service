package com.example.recycling_service.dto.Response;

import com.example.recycling_service.dto.CategoryDto;
import com.example.recycling_service.model.Advertisement;
import com.example.recycling_service.model.Media;
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
public class AdvertisementResponse {
    private UUID id;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    private BigDecimal price;

    private List<String> mediaFilePaths = new ArrayList<>();

    @NotEmpty(message = "At least 1 category required")
    private Set<CategoryDto> categories;

    @NotEmpty(message = "User cannot be empty")
    private UUID userId;

    private String address;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDateTime createdAt;

    // Конструктор для преобразования Entity -> DTO
    public AdvertisementResponse(Advertisement ad) {
        this.id = ad.getId();
        this.title = ad.getTitle();
        this.description = ad.getDescription();
        this.price = ad.getPrice();
        this.categories = ad.getCategories() != null ?
                ad.getCategories().stream()
                        .map(CategoryDto::new)
                        .collect(Collectors.toSet()) :
                Collections.emptySet();
        this.userId = ad.getUser().getId();
        this.address = ad.getAddress();
        this.createdAt = ad.getCreatedAt();
        List<Media> mediaFiles = ad.getMedia();
        List<String> mediaFilePaths = new ArrayList<>(mediaFiles.size());
        for (Media media : mediaFiles) {
            mediaFilePaths.add(media.getId().toString());
        }
        this.mediaFilePaths = mediaFilePaths;
    }
}
