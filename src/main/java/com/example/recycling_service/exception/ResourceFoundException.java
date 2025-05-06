/*  Исключения
    Кастомные исключения для обработки ошибок
*/
package com.example.recycling_service.exception;

public class ResourceFoundException extends RuntimeException {
    public ResourceFoundException(String message) {
        super(message);
    }
}
