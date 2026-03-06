package ru.yandex.practicum.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.dto.warehouse.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * API для склада интернет-магазина.
 */
public interface WarehouseApi {

    /**
     * Добавить новый товар на склад.
     */
    @PutMapping("/api/v1/warehouse")
    ResponseEntity<Void> addNewProductInWarehouse(
            @Valid @RequestBody NewProductInWarehouseRequest product);

    /**
     * Проверить наличие товаров на складе для корзины.
     */
    @PostMapping("/api/v1/warehouse/check")
    ResponseEntity<BookedProductsDto> checkProductQuantityInWarehouse(
            @RequestBody ShoppingCartDto shoppingCart);

    /**
     * Увеличить количество товара на складе.
     */
    @PostMapping("/api/v1/warehouse/add")
    ResponseEntity<Void> addProductToWarehouse(
            @Valid @RequestBody AddProductToWarehouseRequest request);

    /**
     * Получить адрес склада.
     */
    @GetMapping("/api/v1/warehouse/address")
    ResponseEntity<AddressDto> getWarehouseAddress();

    /**
     * Собрать товары для заказа из корзины.
     * Проверяет наличие, уменьшает остаток, создаёт OrderBooking.
     */
    @PostMapping("/api/v1/warehouse/assembly")
    ResponseEntity<BookedProductsDto> assemblyProductForOrderFromShoppingCart(
            @RequestParam UUID shoppingCartId,
            @RequestParam UUID orderId);

    /**
     * Передать товары в доставку.
     * Обновляет информацию о собранном заказе, добавляет идентификатор доставки.
     */
    @PostMapping("/api/v1/warehouse/shipped")
    ResponseEntity<Void> shippedToDelivery(
            @RequestParam UUID orderId,
            @RequestParam UUID deliveryId);

    /**
     * Вернуть товар на склад.
     * Увеличивает доступный остаток товаров.
     */
    @PostMapping("/api/v1/warehouse/return")
    ResponseEntity<Void> returnProduct(
            @RequestBody Map<UUID, Long> products);
}
