package com.example.recycling_service.dto.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class CreateRecyclingPointRequest {
    @NotBlank
    private String name;

    private UUID typeId;

    private String address;

    @NotBlank
    private double latitude;
    @NotBlank
    private double longitude;

    private String phoneNumber;

    private String email;

    @NotBlank
    private List<UUID> categoryIds;
}
