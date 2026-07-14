package com.example.recycling_service.dto.Request;

import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Data
@Getter
public class RecyclePointFilterRequest {
    private List<UUID> categoryIds;
}
