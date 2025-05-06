package com.example.recycling_service.dto;

import com.example.recycling_service.model.Advertisement;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@AllArgsConstructor
@Data
public class UserProfileDto {
    private Long id;
    private String username;
    private String email;
    private String role;
    private List<Advertisement> advertisements;
}