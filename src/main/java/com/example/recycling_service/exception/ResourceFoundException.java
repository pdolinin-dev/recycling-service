/*  Исключения
    Кастомные исключения для обработки ошибок
*/
package com.example.recycling_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

public class ResourceFoundException extends RuntimeException {
    public ResourceFoundException(String message) {
        super(message);
    }
}
