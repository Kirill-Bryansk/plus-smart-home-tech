package ru.yandex.practicum.model.dto.order;

import lombok.Data;
import ru.yandex.practicum.model.dto.warehouse.AddressDto;
import ru.yandex.practicum.model.dto.warehouse.ShoppingCartDto;

import jakarta.validation.constraints.NotNull;

/**
 * Запрос на создание нового заказа.
 */
@Data
public class CreateNewOrderRequest {

    /** Корзина товаров */
    @NotNull(message = "Корзина товаров не должна быть пустой")
    private ShoppingCartDto shoppingCart;

    /** Адрес доставки */
    @NotNull(message = "Адрес доставки не должен быть пустым")
    private AddressDto deliveryAddress;
}
