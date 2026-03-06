package ru.yandex.practicum.model.enums;

/**
 * Статусы заказа в системе.
 */
public enum OrderStatus {
    /** Новый заказ */
    NEW,
    
    /** Ожидает оплаты */
    ON_PAYMENT,
    
    /** Ожидает доставки */
    ON_DELIVERY,
    
    /** Выполнен */
    DONE,
    
    /** Доставлен */
    DELIVERED,
    
    /** Собран */
    ASSEMBLED,
    
    /** Оплачен */
    PAID,
    
    /** Завершён */
    COMPLETED,
    
    /** Неудачная доставка */
    DELIVERY_FAILED,
    
    /** Неудачная сборка */
    ASSEMBLY_FAILED,
    
    /** Неудачная оплата */
    PAYMENT_FAILED,
    
    /** Возврат товаров */
    PRODUCT_RETURNED,
    
    /** Отменён */
    CANCELED
}
