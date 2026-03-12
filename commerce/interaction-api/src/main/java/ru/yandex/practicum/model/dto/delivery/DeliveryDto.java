package ru.yandex.practicum.model.dto.delivery;

import lombok.Data;
import ru.yandex.practicum.model.dto.warehouse.AddressDto;
import ru.yandex.practicum.model.enums.DeliveryStatus;

import java.util.UUID;

/**
 * Представление доставки в системе.
 */
@Data
public class DeliveryDto {

    /** Идентификатор доставки */
    private UUID deliveryId;

    /** Идентификатор заказа */
    private UUID orderId;

    /** Общий объём товаров (м³) */
    private Double volume;

    /** Общий вес товаров (кг) */
    private Double weight;

    /** Признак хрупких товаров */
    private Boolean fragile;

    /** Адрес склада (откуда) */
    private AddressDto addressFrom;

    /** Адрес доставки (куда) */
    private AddressDto addressTo;

    /** Статус доставки */
    private DeliveryStatus status;

    /** Стоимость доставки */
    private Double cost;
}
