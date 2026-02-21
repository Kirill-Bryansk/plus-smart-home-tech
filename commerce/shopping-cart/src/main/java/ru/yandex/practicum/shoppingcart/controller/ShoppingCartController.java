package ru.yandex.practicum.shoppingcart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.dto.warehouse.ShoppingCartDto;
import ru.yandex.practicum.shoppingcart.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    /**
     * Получить корзину пользователя.
     */
    @GetMapping
    public ResponseEntity<ShoppingCartDto> getShoppingCart(@RequestParam String username) {
        log.info("GET /api/v1/shopping-cart?username={}", username);
        ShoppingCartDto cart = shoppingCartService.getShoppingCart(username);
        return ResponseEntity.ok(cart);
    }

    /**
     * Добавить товар в корзину.
     */
    @PutMapping
    public ResponseEntity<ShoppingCartDto> addProductToShoppingCart(
            @RequestParam String username,
            @RequestBody Map<UUID, Long> products) {
        log.info("PUT /api/v1/shopping-cart?username={}", username);
        ShoppingCartDto cart = shoppingCartService.addProductToCart(username, products);
        return ResponseEntity.ok(cart);
    }

    /**
     * Деактивировать корзину.
     */
    @DeleteMapping
    public ResponseEntity<Void> deactivateCurrentShoppingCart(@RequestParam String username) {
        log.info("DELETE /api/v1/shopping-cart?username={}", username);
        shoppingCartService.deactivateCart(username);
        return ResponseEntity.ok().build();
    }

    /**
     * Удалить товары из корзины.
     */
    @PostMapping("/remove")
    public ResponseEntity<ShoppingCartDto> removeFromShoppingCart(
            @RequestParam String username,
            @RequestBody List<UUID> productIds) {
        log.info("POST /api/v1/shopping-cart/remove?username={}", username);
        ShoppingCartDto cart = shoppingCartService.removeFromCart(username, productIds);
        return ResponseEntity.ok(cart);
    }

    /**
     * Изменить количество товара в корзине.
     */
    @PostMapping("/change-quantity")
    public ResponseEntity<ShoppingCartDto> changeProductQuantity(
            @RequestParam String username,
            @Valid @RequestBody ChangeProductQuantityRequest request) {
        log.info("POST /api/v1/shopping-cart/change-quantity?username={}", username);
        ShoppingCartDto cart = shoppingCartService.changeProductQuantity(
                username, request.getProductId(), request.getNewQuantity());
        return ResponseEntity.ok(cart);
    }

    /**
     * DTO для запроса изменения количества товара.
     */
    @lombok.Data
    public static class ChangeProductQuantityRequest {
        private UUID productId;
        private Long newQuantity;
    }
}
