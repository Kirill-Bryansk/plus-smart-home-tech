package ru.yandex.practicum.payment.exception;

/**
 * Исключение, выбрасываемое когда оплата не найдена.
 */
public class NoPaymentFoundException extends RuntimeException {

    public NoPaymentFoundException(String message) {
        super(message);
    }
}
