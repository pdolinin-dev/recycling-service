package com.example.recycling_service.dto;

import com.example.recycling_service.model.Advertisement;
import com.example.recycling_service.model.User;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


//@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserProfileDto {

    @NotNull
    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    private String name;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @NotBlank
    private String password;

    @NotBlank
    private String email;

    @NotBlank
    private String role;
    private List<Advertisement> advertisements;

    public UserProfileDto(Long id, String username, LocalDateTime createdAt, LocalDateTime updatedAt, String password, String name, String email, String role, List<Advertisement> ads) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.password = password;
        this.email = email;
        this.role = role;
        this.advertisements = ads;
    }

//    public UserProfileDto userProfileDto(User user) {
//        this.id = user.getId();
//        this.username = user.getUsername();
//        this.email = user.getEmail();
//        this.role = user.getRole();
//        this.name = user.getName();
//        this.createdAt = user.getCreatedAt();
//        this.updatedAt = LocalDateTime.now();
//        return this;
//    };
}