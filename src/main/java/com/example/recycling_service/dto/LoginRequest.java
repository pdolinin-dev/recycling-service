package com.example.recycling_service.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username; // или userLogin, если используете user_login из БД
    private String password;
}
