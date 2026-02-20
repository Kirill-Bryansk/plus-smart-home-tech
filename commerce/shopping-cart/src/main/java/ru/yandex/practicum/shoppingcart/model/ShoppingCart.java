package ru.yandex.practicum.shoppingcart.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.yandex.practicum.model.enums.CartState;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Корзина пользователя.
 * Сущность совместима с H2 (dev) и PostgreSQL (prod).
 */
@Entity
@Table(name = "shopping_carts")
@Data
public class ShoppingCart {

    @Id
    @Column(name = "shopping_cart_id", columnDefinition = "uuid")
    private UUID shoppingCartId;

    @Column(name = "username", nullable = false, unique = true, length = 32)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "cart_state", length = 10)
    private CartState cartState;

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartProduct> products = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (shoppingCartId == null) {
            shoppingCartId = UUID.randomUUID();
        }
        if (cartState == null) {
            cartState = CartState.ACTIVE;
        }
    }
}
