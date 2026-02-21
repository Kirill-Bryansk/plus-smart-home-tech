package ru.yandex.practicum.shoppingcart.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

/**
 * Товар в корзине.
 */
@Entity
@Table(name = "cart_products", schema = "shopping_cart_schema")
@Data
@IdClass(CartProductId.class)
public class CartProduct {

    @Id
    @Column(name = "shopping_cart_id", columnDefinition = "uuid")
    private UUID shoppingCartId;

    @Id
    @Column(name = "product_id", columnDefinition = "uuid")
    private UUID productId;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_cart_id", insertable = false, updatable = false)
    private ShoppingCart shoppingCart;
}
