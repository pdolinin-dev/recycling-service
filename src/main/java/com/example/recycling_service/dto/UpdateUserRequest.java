package com.example.recycling_service.dto;

import com.example.recycling_service.model.Advertisement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class UpdateUserRequest {

    @Getter
    @Setter
    @NotNull
    private Long id;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String role;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String password;

    private List<Advertisement> advertisements;

    private String avatarPath;
}
