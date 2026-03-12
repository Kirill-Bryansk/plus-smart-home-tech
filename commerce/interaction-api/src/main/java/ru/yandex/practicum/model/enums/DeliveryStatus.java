package ru.yandex.practicum.model.enums;

/**
 * Статусы доставки в системе.
 */
public enum DeliveryStatus {
    /** Создана */
    CREATED,
    
    /** В процессе выполнения */
    IN_PROGRESS,
    
    /** Доставлена */
    DELIVERED,
    
    /** Ошибка доставки */
    FAILED,
    
    /** Отменена */
    CANCELLED
}
