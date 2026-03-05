package ru.yandex.practicum.warehouse.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.api.WarehouseApi;
import ru.yandex.practicum.model.dto.warehouse.*;
import ru.yandex.practicum.warehouse.service.WarehouseService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WarehouseController implements WarehouseApi {

    private final WarehouseService warehouseService;

    @Override
    @PutMapping("/api/v1/warehouse")
    public ResponseEntity<Void> addNewProductInWarehouse(
            @Valid @RequestBody NewProductInWarehouseRequest product) {
        log.info("PUT /api/v1/warehouse - получен запрос на добавление нового товара productId={}", product.getProductId());
        warehouseService.addNewProductInWarehouse(product);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/api/v1/warehouse/check")
    public ResponseEntity<BookedProductsDto> checkProductQuantityInWarehouse(
            @RequestBody ShoppingCartDto shoppingCart) {
        log.info("POST /api/v1/warehouse/check - проверка наличия товаров для корзины cartId={}", shoppingCart.getShoppingCartId());
        BookedProductsDto result = warehouseService.checkProductQuantityInWarehouse(shoppingCart);
        return ResponseEntity.ok(result);
    }

    @Override
    @PostMapping("/api/v1/warehouse/add")
    public ResponseEntity<Void> addProductToWarehouse(
            @Valid @RequestBody AddProductToWarehouseRequest request) {
        log.info("POST /api/v1/warehouse/add - добавление товара на склад productId={}", request.getProductId());
        warehouseService.addProductToWarehouse(request);
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping("/api/v1/warehouse/address")
    public ResponseEntity<AddressDto> getWarehouseAddress() {
        log.info("GET /api/v1/warehouse/address - запрос адреса склада");
        AddressDto address = warehouseService.getWarehouseAddress();
        return ResponseEntity.ok(address);
    }
}