package ru.yandex.practicum.delivery.exception;

/**
 * Исключение, выбрасываемое когда доставка не найдена.
 */
public class NoDeliveryFoundException extends RuntimeException {

    public NoDeliveryFoundException(String message) {
        super(message);
    }
}
