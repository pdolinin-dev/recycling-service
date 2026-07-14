package com.example.recycling_service.dto.Response;

import com.example.recycling_service.model.Enum.Role;
import lombok.Data;

@Data
public class JwtResponse {
    private String login;
    private String role;
    private String jwt;

    public JwtResponse(String login, String role, String jwt) {
        this.login = login;
        this.role = role;
        this.jwt = jwt;
    }
}