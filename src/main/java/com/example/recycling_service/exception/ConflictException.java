package com.example.recycling_service.exception;

public class ConflictException extends RuntimeException {

    public ConflictException(String resourceName, String field) {
        super(resourceName + " с таким " + field + " уже существует");
    }
}
