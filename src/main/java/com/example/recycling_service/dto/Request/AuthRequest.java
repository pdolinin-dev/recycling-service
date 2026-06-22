package com.example.recycling_service.dto.Request;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}