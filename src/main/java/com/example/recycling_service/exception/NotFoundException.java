/*  Исключения
    Кастомные исключения для обработки ошибок
*/
package com.example.recycling_service.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String resourceName, String field, Object id) {
        super("Не найден " + resourceName + " с " + field + ": " + id);
    }
}
