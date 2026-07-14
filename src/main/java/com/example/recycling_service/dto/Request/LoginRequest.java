package com.example.recycling_service.dto.Request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class LoginRequest {

    @NotBlank
    private String login; // или userLogin, если используете user_login из БД

    @NotBlank
    private String password;
}
