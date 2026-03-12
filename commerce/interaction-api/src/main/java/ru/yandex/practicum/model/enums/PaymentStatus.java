package ru.yandex.practicum.model.enums;

/**
 * Статусы оплаты в системе.
 */
public enum PaymentStatus {
    /** Ожидает оплаты */
    PENDING,
    
    /** Успешно оплачен */
    SUCCESS,
    
    /** Ошибка в процессе оплаты */
    FAILED
}
