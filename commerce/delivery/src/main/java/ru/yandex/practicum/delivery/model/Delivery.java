package ru.yandex.practicum.delivery.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.model.enums.DeliveryStatus;

import java.util.Objects;
import java.util.UUID;

/**
 * Сущность доставки.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "deliveries", schema = "delivery_schema")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "delivery_id", nullable = false)
    private UUID deliveryId;

    /** Идентификатор заказа */
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    /** Общий объём товаров (м³) */
    @Column(name = "volume")
    private Double volume;

    /** Общий вес товаров (кг) */
    @Column(name = "weight")
    private Double weight;

    /** Признак хрупких товаров */
    @Column(name = "fragile")
    @Builder.Default
    private Boolean fragile = false;

    /** Адрес склада (откуда) */
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "country", column = @Column(name = "from_country")),
        @AttributeOverride(name = "city", column = @Column(name = "from_city")),
        @AttributeOverride(name = "street", column = @Column(name = "from_street")),
        @AttributeOverride(name = "house", column = @Column(name = "from_house")),
        @AttributeOverride(name = "flat", column = @Column(name = "from_flat"))
    })
    private Address addressFrom;

    /** Адрес доставки (куда) */
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "country", column = @Column(name = "to_country")),
        @AttributeOverride(name = "city", column = @Column(name = "to_city")),
        @AttributeOverride(name = "street", column = @Column(name = "to_street")),
        @AttributeOverride(name = "house", column = @Column(name = "to_house")),
        @AttributeOverride(name = "flat", column = @Column(name = "to_flat"))
    })
    private Address addressTo;

    /** Статус доставки */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private DeliveryStatus status = DeliveryStatus.CREATED;

    /** Стоимость доставки */
    @Column(name = "cost")
    private Double cost;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Delivery delivery = (Delivery) o;
        return Objects.equals(deliveryId, delivery.deliveryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deliveryId);
    }
}
