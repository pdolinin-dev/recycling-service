package com.example.recycling_service.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UpdateAdvertisementRequest {
    @NotBlank(message = "Title cannot be blank (if provided)")
    private String title;  // Опционально (если null - не обновляем)

    @NotBlank(message = "Description cannot be blank (if provided)")
    private String description;  // Опционально

    @Positive(message = "Price must be positive (if provided)")
    private BigDecimal price;  // Опционально

    private List<UUID> categoryIds;
    // Можно добавить другие поля (например, список изображений)
}
