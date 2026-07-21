/*  Исключения
    Кастомные исключения для обработки ошибок
*/
package com.example.recycling_service.exception;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class NotFoundException extends RuntimeException {
    Logger logger = LoggerFactory.getLogger(NotFoundException.class);

    public NotFoundException(String resourceName, String field, Object id) {
        super("Не найден " + resourceName + " с " + field + ": " + id);
        logger.error("Не найден {} с {}: {}", resourceName, field, id);
    }
}
