package ru.yandex.practicum.shoppingstore.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.dto.store.ProductDto;
import ru.yandex.practicum.model.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.model.enums.ProductCategory;
import ru.yandex.practicum.shoppingstore.service.ShoppingStoreService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController {

    private final ShoppingStoreService shoppingStoreService;

    /**
     * Получить товары категории.
     */
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getProducts(
            @RequestParam ProductCategory category,
            Pageable pageable) {

        log.info("GET /api/v1/shopping-store?category={}, page={}, size={}, sort={}",
                category,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        // Если сортировка не указана, добавляем сортировку по умолчанию (productName DESC)
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "productName")
            );
        }

        Page<ProductDto> products = shoppingStoreService.findAllByProductCategory(category, pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Получить товар по ID.
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable UUID productId) {
        log.info("Запрос товара с ID: {}", productId);
        ProductDto product = shoppingStoreService.findProductById(productId);
        return ResponseEntity.ok(product);
    }

    /**
     * Создать новый товар.
     */
    @PutMapping
    public ResponseEntity<ProductDto> createNewProduct(@Valid @RequestBody ProductDto dto) {
        log.info("Создание нового товара: {}", dto.getProductName());
        ProductDto created = shoppingStoreService.addNewProduct(dto);
        return ResponseEntity.ok(created);
    }

    /**
     * Обновить товар.
     */
    @PostMapping
    public ResponseEntity<ProductDto> updateProduct(@Valid @RequestBody ProductDto dto) {
        log.info("Обновление товара с ID: {}", dto.getProductId());
        ProductDto updated = shoppingStoreService.updateProduct(dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Удалить товар.
     */
    @PostMapping("/removeProductFromStore")
    public ResponseEntity<Boolean> removeProductFromStore(@RequestBody UUID productId) {
        log.info("Удаление товара с ID: {}", productId);
        Boolean removed = shoppingStoreService.removeProductById(productId);
        return ResponseEntity.ok(removed);
    }

    /**
     * Установить статус количества товара.
     */
    @PostMapping("/quantityState")
    public ResponseEntity<Boolean> setProductQuantityState(
            @RequestParam UUID productId,
            @RequestParam String quantityState) {
        
        log.info("POST /api/v1/shopping-store/quantityState productId={} quantityState={}", 
                productId, quantityState);
        
        try {
            // Конвертируем String в Enum
            SetProductQuantityStateRequest request = new SetProductQuantityStateRequest(
                    productId, 
                    ru.yandex.practicum.model.enums.QuantityState.valueOf(quantityState)
            );
            
            Boolean updated = shoppingStoreService.setProductQuantityState(request);
            log.info("Статус количества обновлён: {}", updated);
            return ResponseEntity.ok(updated);
            
        } catch (IllegalArgumentException e) {
            log.error(" Неверное значение quantityState: {}. Допустимые значения: ENDED, FEW, ENOUGH, MANY",
                    quantityState);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(" Ошибка при обновлении статуса количества: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
