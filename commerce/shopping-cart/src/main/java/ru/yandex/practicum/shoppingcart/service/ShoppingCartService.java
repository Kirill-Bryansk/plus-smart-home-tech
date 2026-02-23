package ru.yandex.practicum.shoppingcart.service;

import ru.yandex.practicum.model.dto.warehouse.ShoppingCartDto;

import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {

    /**
     * Получить корзину пользователя.
     */
    ShoppingCartDto getShoppingCart(String username);

    /**
     * Добавить товар в корзину.
     */
    ShoppingCartDto addProductToCart(String username, Map<UUID, Long> products);

    /**
     * Деактивировать корзину.
     */
    void deactivateCart(String username);

    /**
     * Удалить товары из корзины.
     */
    ShoppingCartDto removeFromCart(String username, java.util.List<UUID> productIds);

    /**
     * Изменить количество товара в корзине.
     */
    ShoppingCartDto changeProductQuantity(String username, UUID productId, Long newQuantity);
}
