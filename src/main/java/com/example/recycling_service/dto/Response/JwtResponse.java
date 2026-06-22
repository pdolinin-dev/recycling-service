package com.example.recycling_service.dto.Response;

import lombok.Data;

@Data
public class JwtResponse {
    private String username;
    private String role;

    public JwtResponse(String username, String role) {
        this.username = username;
        this.role = role;
    }
}