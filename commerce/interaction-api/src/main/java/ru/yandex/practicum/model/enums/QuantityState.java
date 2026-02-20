package ru.yandex.practicum.model.enums;

/**
 * Статус остатка товара на складе.
 * Используется в shopping-store и warehouse.
 */
public enum QuantityState {
    ENDED,    // товар закончился (0 единиц)
    FEW,      // товара мало (1-5 единиц)
    ENOUGH,   // товара достаточно (6-50 единиц)
    MANY      // товара много (более 50 единиц)
}
