package ru.yandex.practicum.model.enums;

/**
 * Состояние корзины.
 * Используется в shopping-cart.
 */
public enum CartState {
    ACTIVE,     // активна, можно добавлять товары
    INACTIVE,   // неактивна (например, после оформления заказа)
    DELETED     // удалена
}
