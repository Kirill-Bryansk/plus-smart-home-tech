package ru.yandex.practicum.payment.service;

import ru.yandex.practicum.model.dto.order.OrderDto;
import ru.yandex.practicum.model.dto.payment.PaymentDto;

import java.util.UUID;

/**
 * Сервис для управления оплатой.
 */
public interface PaymentService {

    /**
     * Рассчитать стоимость товаров в заказе.
     * Умножает количество каждого товара на цену из shopping-store.
     */
    Double productCost(OrderDto orderDto);

    /**
     * Рассчитать общую стоимость заказа (товары + доставка + НДС 10%).
     */
    Double getTotalCost(OrderDto orderDto);

    /**
     * Подготовить и сформировать сведения по оплате (сохранить в БД).
     */
    PaymentDto payment(OrderDto orderDto);

    /**
     * Проставить признак успешной оплаты.
     * Изменяет статус оплаты на SUCCESS и обновляет заказ.
     */
    PaymentDto paymentSuccess(UUID paymentId);

    /**
     * Проставить признак отказа в оплате.
     * Изменяет статус оплаты на FAILED и обновляет заказ.
     */
    PaymentDto paymentFailed(UUID paymentId);
}
