package com.example.recycling_service.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;
import java.util.List;


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
    private Set<Long> categoryIds;

    private List<String> imageUrls;
}