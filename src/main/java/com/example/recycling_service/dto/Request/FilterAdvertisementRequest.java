package com.example.recycling_service.dto.Request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class FilterAdvertisementRequest {
    private List<UUID> categoryIds;
}
