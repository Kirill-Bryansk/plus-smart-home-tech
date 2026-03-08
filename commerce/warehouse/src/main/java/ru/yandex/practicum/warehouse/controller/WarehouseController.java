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
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseApi {

    private final WarehouseService warehouseService;

    /**
     * Добавить новый товар на склад.
     */
    @Override
    @PutMapping
    public ResponseEntity<Void> addNewProductInWarehouse(
            @Valid @RequestBody NewProductInWarehouseRequest product) {
        log.info("PUT - добавление нового товара productId={}", product.getProductId());
        warehouseService.addNewProductInWarehouse(product);
        return ResponseEntity.ok().build();
    }

    /**
     * Проверить наличие товаров на складе и зарезервировать их.
     */
    @Override
    @PostMapping("/check")
    public ResponseEntity<BookedProductsDto> checkProductQuantityInWarehouse(
            @RequestBody ShoppingCartDto shoppingCart) {
        log.info("POST /check - проверка наличия товаров для корзины cartId={}", shoppingCart.getShoppingCartId());
        BookedProductsDto result = warehouseService.checkProductQuantityInWarehouse(shoppingCart);
        return ResponseEntity.ok(result);
    }

    /**
     * Добавить количество существующего товара на складе.
     */
    @Override
    @PostMapping("/add")
    public ResponseEntity<Void> addProductToWarehouse(
            @Valid @RequestBody AddProductToWarehouseRequest request) {
        log.info("POST /add - добавление товара на склад productId={}", request.getProductId());
        warehouseService.addProductToWarehouse(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Получить адрес склада.
     */
    @Override
    @GetMapping("/address")
    public ResponseEntity<AddressDto> getWarehouseAddress() {
        log.info("GET /address - запрос адреса склада");
        AddressDto address = warehouseService.getWarehouseAddress();
        return ResponseEntity.ok(address);
    }

    /**
     * Собрать товары из корзины в заказ.
     */
    @Override
    @PostMapping("/assembly")
    public ResponseEntity<BookedProductsDto> assemblyProductForOrderFromShoppingCart(
            @RequestParam UUID shoppingCartId,
            @RequestParam UUID orderId) {
        log.info("POST /assembly - сборка товаров для заказа: cartId={}, orderId={}",
                shoppingCartId, orderId);
        BookedProductsDto result = warehouseService.assemblyProductForOrderFromShoppingCart(shoppingCartId, orderId);
        return ResponseEntity.ok(result);
    }

    /**
     * Подтвердить передачу заказа в доставку.
     */
    @Override
    @PostMapping("/shipped")
    public ResponseEntity<Void> shippedToDelivery(
            @RequestParam UUID orderId,
            @RequestParam UUID deliveryId) {
        log.info("POST /shipped - передача в доставку: orderId={}, deliveryId={}",
                orderId, deliveryId);
        warehouseService.shippedToDelivery(orderId, deliveryId);
        return ResponseEntity.ok().build();
    }

    /**
     * Принять возврат товаров на склад.
     */
    @Override
    @PostMapping("/return")
    public ResponseEntity<Void> returnProduct(
            @RequestBody Map<UUID, Long> products) {
        log.info("POST /return - возврат товаров на склад: {} позиций", products.size());
        warehouseService.returnProduct(products);
        return ResponseEntity.ok().build();
    }
}