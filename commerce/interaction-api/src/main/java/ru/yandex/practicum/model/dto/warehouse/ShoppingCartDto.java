package ru.yandex.practicum.model.dto.warehouse;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

/**
 * Корзина товаров в онлайн магазине.
 */
@Data
public class ShoppingCartDto {

    @NotNull(message = "Идентификатор корзины не должен быть пустым")
    private UUID shoppingCartId;

    @NotEmpty(message = "Корзина не должна быть пустой")
    private Map<UUID, Long> products;
}
