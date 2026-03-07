package ru.yandex.practicum.delivery.service;

import ru.yandex.practicum.model.dto.delivery.DeliveryDto;
import ru.yandex.practicum.model.dto.order.OrderDto;

import java.util.UUID;

/**
 * Сервис для управления доставкой.
 */
public interface DeliveryService {

    /**
     * Создать новую доставку в БД.
     */
    DeliveryDto planDelivery(DeliveryDto deliveryDto);

    /**
     * Рассчитать стоимость доставки для заказа.
     * Алгоритм:
     * - Базовая стоимость = 5.0
     * - Умножаем на коэффициент адреса склада (ADDRESS_1=1, ADDRESS_2=2)
     * - Если хрупкий, умножаем на 0.2 и прибавляем
     * - Добавляем вес * 0.3
     * - Добавляем объём * 0.2
     * - Если улица доставки не совпадает со складом, умножаем на 0.2 и прибавляем
     */
    Double deliveryCost(OrderDto orderDto);

    /**
     * Принять товары в доставку (изменить статус на IN_PROGRESS).
     * Также вызывает warehouse.shippedToDelivery.
     */
    DeliveryDto acceptToDelivery(UUID orderId);

    /**
     * Успешная доставка.
     * Изменяет статус доставки на DELIVERED и обновляет заказ.
     */
    DeliveryDto deliverySuccess(UUID orderId);

    /**
     * Ошибка доставки.
     * Изменяет статус доставки на FAILED и обновляет заказ.
     */
    DeliveryDto deliveryFailed(UUID orderId);
}
