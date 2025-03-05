package com.example.recycling_service.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TokenService {

    private final Map<String, String> tokenStore = new HashMap<>();

    // Генерация токена, сохранение токена в храналище
    public String generateToken(String username) {
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, username);
        return token;
    }

    //Проверка наличия токена
    public boolean validateToken(String token) {
        return tokenStore.containsKey(token);
    }

    // получаем Имя пользователя по токены
    public String getUsernameByToken(String token) {
        return tokenStore.get(token);
    }

}
