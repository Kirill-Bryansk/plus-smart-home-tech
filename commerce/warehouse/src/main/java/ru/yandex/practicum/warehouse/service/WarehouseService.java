package ru.yandex.practicum.warehouse.service;

import ru.yandex.practicum.model.dto.warehouse.*;

import java.util.List;
import java.util.Map;
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

    /**
     * Собрать товары для заказа из корзины.
     * Проверяет наличие, уменьшает остаток, создаёт OrderBooking.
     */
    BookedProductsDto assemblyProductForOrderFromShoppingCart(UUID shoppingCartId, UUID orderId);

    /**
     * Передать товары в доставку.
     * Обновляет информацию о собранном заказе, добавляет идентификатор доставки.
     */
    void shippedToDelivery(UUID orderId, UUID deliveryId);

    /**
     * Вернуть товар на склад.
     * Увеличивает доступный остаток товаров.
     */
    void returnProduct(Map<UUID, Long> products);
}
