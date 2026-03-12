package ru.yandex.practicum.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение: пользователь не авторизован.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NotAuthorizedUserException extends RuntimeException {

    public NotAuthorizedUserException() {
        super("Имя пользователя не должно быть пустым");
    }

    public NotAuthorizedUserException(String message) {
        super(message);
    }
}
