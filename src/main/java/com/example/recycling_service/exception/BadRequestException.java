package com.example.recycling_service.exception;

import java.util.List;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String field) {
        super("Поле " + field + " не может быть пустым");
    }
}
