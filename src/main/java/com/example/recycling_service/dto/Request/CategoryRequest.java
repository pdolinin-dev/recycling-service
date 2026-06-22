package com.example.recycling_service.dto.Request;

import com.example.recycling_service.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class  CategoryRequest {
    private UUID id;
    private String name; // если нужно

    public CategoryRequest(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }
}
