package com.example.recycling_service.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String username) {
        super("У пользователя: " + username + " нет прав для этого действия");
    }
}
