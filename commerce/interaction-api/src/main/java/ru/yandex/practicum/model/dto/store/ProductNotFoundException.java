package ru.yandex.practicum.model.dto.store;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * Исключение: товар не найден.
 */
@Data
public class ProductNotFoundException {

    private HttpStatus httpStatus;
    private String userMessage;
    private String message;
    private Instant timestamp;
}
