package com.example.recycling_service.dto.Request;

import com.example.recycling_service.model.Advertisement;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Data
public class UpdateUserRequest {

    @Getter
    @Setter
    private String login;

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
