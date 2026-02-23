package ru.yandex.practicum.shoppingstore.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.model.dto.store.ProductDto;
import ru.yandex.practicum.model.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.model.enums.ProductCategory;

import java.util.UUID;

public interface ShoppingStoreService {

    /**
     * Получить товары по категории с пагинацией.
     */
    Page<ProductDto> findAllByProductCategory(ProductCategory productCategory, Pageable pageable);

    /**
     * Получить товар по ID.
     */
    ProductDto findProductById(UUID productId);

    /**
     * Добавить новый товар.
     */
    ProductDto addNewProduct(ProductDto dto);

    /**
     * Обновить товар.
     */
    ProductDto updateProduct(ProductDto dto);

    /**
     * Удалить товар (деактивировать).
     */
    Boolean removeProductById(UUID productId);

    /**
     * Установить статус количества товара.
     */
    Boolean setProductQuantityState(SetProductQuantityStateRequest request);
}
