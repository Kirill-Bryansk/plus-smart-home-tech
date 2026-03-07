package ru.yandex.practicum.warehouse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

/**
 * Сущность забронированных товаров для заказа.
 * Создаётся при сборке заказа и хранит информацию о зарезервированных товарах.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "order_booking", schema = "warehouse_schema")
public class OrderBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "booking_id", nullable = false)
    private UUID bookingId;

    /** Идентификатор заказа */
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    /** Идентификатор корзины */
    @Column(name = "shopping_cart_id", nullable = false)
    private UUID shoppingCartId;

    /** Идентификатор доставки */
    @Column(name = "delivery_id")
    private UUID deliveryId;

    /** Общий вес товаров */
    @Column(name = "total_weight")
    private Double totalWeight;

    /** Общий объём товаров */
    @Column(name = "total_volume")
    private Double totalVolume;

    /** Признак хрупкости */
    @Column(name = "fragile")
    @Builder.Default
    private Boolean fragile = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderBooking that = (OrderBooking) o;
        return Objects.equals(bookingId, that.bookingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }
}
