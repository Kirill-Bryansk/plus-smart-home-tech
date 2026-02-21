package ru.yandex.practicum.warehouse.service;

import ru.yandex.practicum.model.dto.warehouse.*;

import java.util.UUID;

public interface WarehouseService {

    /**
     * Добавить новый товар на склад.
     */
    void addNewProductInWarehouse(NewProductInWarehouseRequest request);

    /**
     * Проверить наличие товаров на складе для корзины.
     */
    BookedProductsDto checkProductQuantityInWarehouse(ShoppingCartDto shoppingCart);

    /**
     * Увеличить количество товара на складе.
     */
    void addProductToWarehouse(AddProductToWarehouseRequest request);

    /**
     * Получить адрес склада.
     */
    AddressDto getWarehouseAddress();
}
