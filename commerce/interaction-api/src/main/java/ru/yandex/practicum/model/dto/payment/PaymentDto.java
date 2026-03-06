package ru.yandex.practicum.model.dto.payment;

import lombok.Data;
import ru.yandex.practicum.model.enums.PaymentStatus;

import java.util.UUID;

/**
 * Представление оплаты в системе.
 */
@Data
public class PaymentDto {

    /** Идентификатор оплаты */
    private UUID paymentId;

    /** Идентификатор заказа */
    private UUID orderId;

    /** Стоимость товаров */
    private Double productCost;

    /** Стоимость доставки */
    private Double deliveryCost;

    /** Общая стоимость (с учётом НДС) */
    private Double totalCost;

    /** Статус оплаты */
    private PaymentStatus status;
}
