package com.example.recycling_service.dto;

import com.example.recycling_service.model.Advertisement;
import com.example.recycling_service.model.Enum.Role;
import com.example.recycling_service.model.User;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


//@NoArgsConstructor
//@AllArgsConstructor
@Data
public class UserProfileDto {
    
    @NotNull
    private UUID id;

    @NotBlank
    private String login;

    @NotBlank
    private String name;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @NotBlank
    private String email;

    private List<Advertisement> advertisements;

//    private String avatarPath;

    public UserProfileDto(UUID id, String login, String name, LocalDateTime createdAt,
                          LocalDateTime updatedAt,
                          String email, List<Advertisement> ads) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.email = email;
        this.advertisements = ads;
//        this.avatarPath = avatarPath;
    }
}