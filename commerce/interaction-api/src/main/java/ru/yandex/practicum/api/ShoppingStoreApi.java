package ru.yandex.practicum.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.model.dto.store.ProductDto;

import java.util.UUID;

/**
 * Feign-клиент для сервиса Shopping Store.
 * Используется сервисом Payment для получения цены товара.
 */
@FeignClient(name = "shopping-store")
public interface ShoppingStoreApi {

    /**
     * Получить товар по идентификатору.
     */
    @GetMapping("/api/v1/shopping-store/products/{id}")
    ResponseEntity<ProductDto> getProduct(@PathVariable UUID id);
}
