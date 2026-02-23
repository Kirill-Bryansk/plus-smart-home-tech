package ru.yandex.practicum.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.dto.warehouse.*;

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
}
