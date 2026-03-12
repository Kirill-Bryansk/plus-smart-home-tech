package ru.yandex.practicum.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение: заказ не найден.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoOrderFoundException extends RuntimeException {

    public NoOrderFoundException() {
        super("Заказ не найден");
    }

    public NoOrderFoundException(String message) {
        super(message);
    }
}
