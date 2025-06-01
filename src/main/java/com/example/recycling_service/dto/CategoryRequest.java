package com.example.recycling_service.dto;

import com.example.recycling_service.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class  CategoryRequest {
    private Long id;
    private String name; // если нужно

    public CategoryRequest(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }
}
