package ru.yandex.practicum.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.enums.OrderStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Сущность заказа.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity

@Table(name = "orders", schema = "order_schema")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    /** Идентификатор корзины, из которой создан заказ */
    @Column(name = "shopping_cart_id", nullable = false)
    private UUID shoppingCartId;

    /** Статус заказа */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    @Builder.Default
    private OrderStatus state = OrderStatus.NEW;

    /** Идентификатор оплаты */
    @Column(name = "payment_id")
    private UUID paymentId;

    /** Идентификатор доставки */
    @Column(name = "delivery_id")
    private UUID deliveryId;

    /** Общий вес товаров (кг) */
    @Column(name = "delivery_weight")
    private Double deliveryWeight;

    /** Общий объём товаров (м³) */
    @Column(name = "delivery_volume")
    private Double deliveryVolume;

    /** Признак хрупкости */
    @Column(name = "fragile")
    @Builder.Default
    private Boolean fragile = false;

    /** Стоимость товаров */
    @Column(name = "product_price")
    private Double productPrice;

    /** Стоимость доставки */
    @Column(name = "delivery_price")
    private Double deliveryPrice;

    /** Общая стоимость */
    @Column(name = "total_price")
    private Double totalPrice;

    /** Товары заказа */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Добавить товар в заказ.
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    /**
     * Удалить товар из заказа.
     */
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}
