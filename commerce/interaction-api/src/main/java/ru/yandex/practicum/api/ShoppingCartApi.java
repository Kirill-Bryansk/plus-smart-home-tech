package ru.yandex.practicum.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.dto.warehouse.ShoppingCartDto;

import java.util.UUID;

/**
 * Feign-клиент для сервиса Shopping Cart.
 * Используется сервисом Warehouse для получения товаров корзины.
 */
@FeignClient(name = "shopping-cart")
public interface ShoppingCartApi {

    /**
     * Получить корзину по идентификатору.
     */
    @GetMapping("/api/v1/shopping-cart/{id}")
    ResponseEntity<ShoppingCartDto> getShoppingCart(@PathVariable UUID id);
}
