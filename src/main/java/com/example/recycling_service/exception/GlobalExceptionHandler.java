package com.example.recycling_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(ResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handlerResourceNotFound(ResourceFoundException e) {
        return e.getMessage();
    }
}
