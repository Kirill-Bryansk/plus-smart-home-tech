package ru.yandex.practicum.model.dto.order;

import lombok.Data;
import ru.yandex.practicum.model.enums.OrderStatus;

import java.util.Map;
import java.util.UUID;

/**
 * Представление заказа в системе.
 */
@Data
public class OrderDto {

    /** Идентификатор заказа */
    private UUID orderId;

    /** Идентификатор корзины */
    private UUID shoppingCartId;

    /** Отображение идентификатора товара на отобранное количество */
    private Map<UUID, Long> products;

    /** Идентификатор оплаты */
    private UUID paymentId;

    /** Идентификатор доставки */
    private UUID deliveryId;

    /** Статус заказа */
    private OrderStatus state;

    /** Общий вес доставки */
    private Double deliveryWeight;

    /** Общий объём доставки */
    private Double deliveryVolume;

    /** Признак хрупкости заказа */
    private Boolean fragile;

    /** Общая стоимость */
    private Double totalPrice;

    /** Стоимость доставки */
    private Double deliveryPrice;

    /** Стоимость товаров в заказе */
    private Double productPrice;
}
