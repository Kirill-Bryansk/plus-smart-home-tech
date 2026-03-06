package ru.yandex.practicum.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

/**
 * Товар в заказе.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "order_items", schema = "order_schema")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    /** Ссылка на заказ */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @ToString.Exclude
    private Order order;

    /** Идентификатор товара */
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    /** Количество товара */
    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(itemId, orderItem.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }
}
