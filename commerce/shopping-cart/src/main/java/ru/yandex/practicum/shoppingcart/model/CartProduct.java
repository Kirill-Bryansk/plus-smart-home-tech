package ru.yandex.practicum.shoppingcart.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;
import java.util.UUID;

/**
 * Товар в корзине.
 */
@Entity
@Table(name = "cart_products", schema = "shopping_cart_schema")
@IdClass(CartProductId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ShoppingCart shoppingCart;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartProduct that = (CartProduct) o;
        return Objects.equals(shoppingCartId, that.shoppingCartId) &&
               Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shoppingCartId, productId);
    }

    @Override
    public String toString() {
        return "CartProduct{" +
               "shoppingCartId=" + shoppingCartId +
               ", productId=" + productId +
               ", quantity=" + quantity +
               '}';
    }
}
