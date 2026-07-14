package com.example.recycling_service.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAdvertisementRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private BigDecimal price;

    @NotEmpty
    private Set<UUID> categoryIds;

    private List<String> imageUrls;
}