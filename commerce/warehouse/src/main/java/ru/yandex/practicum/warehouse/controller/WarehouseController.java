package ru.yandex.practicum.warehouse.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.api.WarehouseApi;
import ru.yandex.practicum.model.dto.warehouse.*;
import ru.yandex.practicum.warehouse.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

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

    @Override
    @PostMapping("/api/v1/warehouse/assembly")
    public ResponseEntity<BookedProductsDto> assemblyProductForOrderFromShoppingCart(
            @RequestParam UUID shoppingCartId,
            @RequestParam UUID orderId) {
        log.info("POST /api/v1/warehouse/assembly - сборка товаров для заказа: shoppingCartId={}, orderId={}", 
            shoppingCartId, orderId);
        BookedProductsDto result = warehouseService.assemblyProductForOrderFromShoppingCart(shoppingCartId, orderId);
        return ResponseEntity.ok(result);
    }

    @Override
    @PostMapping("/api/v1/warehouse/shipped")
    public ResponseEntity<Void> shippedToDelivery(
            @RequestParam UUID orderId,
            @RequestParam UUID deliveryId) {
        log.info("POST /api/v1/warehouse/shipped - передача в доставку: orderId={}, deliveryId={}", 
            orderId, deliveryId);
        warehouseService.shippedToDelivery(orderId, deliveryId);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/api/v1/warehouse/return")
    public ResponseEntity<Void> returnProduct(
            @RequestBody Map<UUID, Long> products) {
        log.info("POST /api/v1/warehouse/return - возврат товаров на склад: количество={}", products.size());
        warehouseService.returnProduct(products);
        return ResponseEntity.ok().build();
    }
}