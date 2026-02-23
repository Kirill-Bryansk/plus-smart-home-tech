package ru.yandex.practicum.shoppingcart.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Составной первичный ключ для CartProduct.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartProductId implements Serializable {

    private UUID shoppingCartId;
    private UUID productId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartProductId that = (CartProductId) o;
        return Objects.equals(shoppingCartId, that.shoppingCartId) &&
               Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shoppingCartId, productId);
    }
}
