package com.example.recycling_service.dto;

import com.example.recycling_service.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private UUID id;
    private String name;

    public CategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }
}
