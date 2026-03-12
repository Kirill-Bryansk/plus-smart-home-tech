package ru.yandex.practicum.payment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.model.enums.PaymentStatus;

import java.util.Objects;
import java.util.UUID;

/**
 * Сущность оплаты.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payments", schema = "payment_schema")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    /** Идентификатор заказа */
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    /** Стоимость товаров */
    @Column(name = "product_cost")
    private Double productCost;

    /** Стоимость доставки */
    @Column(name = "delivery_cost")
    private Double deliveryCost;

    /** Общая стоимость (с учётом НДС) */
    @Column(name = "total_cost")
    private Double totalCost;

    /** Статус оплаты */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(paymentId, payment.paymentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentId);
    }
}
