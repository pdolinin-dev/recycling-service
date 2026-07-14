package com.example.recycling_service.exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException() {
        super("Неправильные логин или пароль");
    }
}
