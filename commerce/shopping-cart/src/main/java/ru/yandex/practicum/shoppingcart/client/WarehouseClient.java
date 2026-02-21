package ru.yandex.practicum.shoppingcart.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.dto.warehouse.*;

/**
 * Feign клиент для вызова warehouse сервиса.
 */
@FeignClient(name = "warehouse")
public interface WarehouseClient {

    /**
     * Проверить наличие товаров на складе для корзины.
     */
    @PostMapping("/api/v1/warehouse/check")
    ResponseEntity<BookedProductsDto> checkProductQuantityInWarehouse(
            @RequestBody ShoppingCartDto shoppingCart);
}
