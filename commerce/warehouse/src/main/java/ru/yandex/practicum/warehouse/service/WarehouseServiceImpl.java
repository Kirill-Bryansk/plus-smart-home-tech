package ru.yandex.practicum.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.ShoppingCartApi;
import ru.yandex.practicum.model.dto.warehouse.*;
import ru.yandex.practicum.warehouse.exception.ProductNotInWarehouseException;
import ru.yandex.practicum.warehouse.mapper.WarehouseMapper;
import ru.yandex.practicum.warehouse.model.OrderBooking;
import ru.yandex.practicum.warehouse.model.WarehouseProduct;
import ru.yandex.practicum.warehouse.repository.OrderBookingRepository;
import ru.yandex.practicum.warehouse.repository.WarehouseProductRepository;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseProductRepository repository;
    private final WarehouseMapper mapper;
    private final OrderBookingRepository bookingRepository;
    private final ShoppingCartApi shoppingCartApi;

    // Случайный адрес склада (выбирается при инициализации)
    private static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[new Random().nextInt(ADDRESSES.length)];

    @Transactional
    @Override
    public void addNewProductInWarehouse(NewProductInWarehouseRequest request) {
        log.info("Добавление нового товара на склад: productId={}", request.getProductId());

        // Проверяем, что товара ещё нет на складе
        if (repository.existsById(request.getProductId())) {
            throw new ProductNotInWarehouseException(
                    "Товар с productId=" + request.getProductId() + " уже есть на складе");
        }

        WarehouseProduct product = mapper.toEntity(request);
        product.setQuantity(0L); // Изначально количество = 0
        repository.save(product);

        log.info("Товар добавлен на склад: productId={}", request.getProductId());
    }

    @Transactional(readOnly = true)
    @Override
    public BookedProductsDto checkProductQuantityInWarehouse(ShoppingCartDto shoppingCart) {
        log.info("Проверка наличия товаров для корзины: cartId={}", shoppingCart.getShoppingCartId());

        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean hasFragile = false;

        for (var entry : shoppingCart.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Long requiredQuantity = entry.getValue();

            WarehouseProduct product = repository.findById(productId)
                    .orElseThrow(() -> new ProductNotInWarehouseException(
                            "Товар с productId=" + productId + " не найден на складе"));

            if (product.getQuantity() < requiredQuantity) {
                throw new ProductNotInWarehouseException(
                        "Недостаточно товара productId=" + productId + 
                        ". Требуется: " + requiredQuantity + 
                        ", доступно: " + product.getQuantity());
            }

            // Считаем общий вес и объём
            totalWeight += product.getWeight() * requiredQuantity;
            totalVolume += calculateVolume(product) * requiredQuantity;
            if (product.getFragile()) {
                hasFragile = true;
            }
        }

        BookedProductsDto result = new BookedProductsDto();
        result.setDeliveryWeight(totalWeight);
        result.setDeliveryVolume(totalVolume);
        result.setFragile(hasFragile);

        log.info("Проверка завершена: weight={}, volume={}, fragile={}", 
                totalWeight, totalVolume, hasFragile);
        return result;
    }

    @Transactional
    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        log.info("Увеличение количества товара на складе: productId={}, quantity={}", 
                request.getProductId(), request.getQuantity());

        WarehouseProduct product = repository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotInWarehouseException(
                        "Товар с productId=" + request.getProductId() + " не найден на складе"));

        product.setQuantity(product.getQuantity() + request.getQuantity());
        repository.save(product);

        log.info("Количество товара обновлено: productId={}, новое количество={}", 
                request.getProductId(), product.getQuantity());
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.info("Запрос адреса склада: {}", CURRENT_ADDRESS);

        AddressDto address = new AddressDto();
        // Дублируем ADDRESS_X в каждое поле
        address.setCountry(CURRENT_ADDRESS);
        address.setCity(CURRENT_ADDRESS);
        address.setStreet(CURRENT_ADDRESS);
        address.setHouse(CURRENT_ADDRESS);
        address.setFlat(CURRENT_ADDRESS);

        return address;
    }

    // Вспомогательный метод для расчёта объёма товара
    private double calculateVolume(WarehouseProduct product) {
        return product.getWidth() * product.getHeight() * product.getDepth();
    }

    @Override
    @Transactional
    public BookedProductsDto assemblyProductForOrderFromShoppingCart(UUID shoppingCartId, UUID orderId) {
        log.info("Сборка товаров для заказа: shoppingCartId={}, orderId={}", shoppingCartId, orderId);

        // 1. Получаем корзину из shopping-cart
        ShoppingCartDto shoppingCart = shoppingCartApi.getShoppingCart(shoppingCartId).getBody();
        if (shoppingCart == null || shoppingCart.getProducts() == null || shoppingCart.getProducts().isEmpty()) {
            throw new ProductNotInWarehouseException("Корзина пуста или не найдена: " + shoppingCartId);
        }

        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean hasFragile = false;

        // 2. Проверяем наличие и списываем товары со склада
        for (Map.Entry<UUID, Long> entry : shoppingCart.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();

            WarehouseProduct product = repository.findById(productId)
                    .orElseThrow(() -> new ProductNotInWarehouseException(
                            "Товар не найден на складе: productId=" + productId));

            if (product.getQuantity() < quantity) {
                throw new ProductNotInWarehouseException(
                        "Недостаточно товара: productId=" + productId +
                        ", требуется=" + quantity +
                        ", доступно=" + product.getQuantity());
            }

            // 3. Списываем товар со склада
            product.setQuantity(product.getQuantity() - quantity);
            repository.save(product);

            // 4. Считаем общий вес и объём
            totalWeight += product.getWeight() * quantity;
            totalVolume += calculateVolume(product) * quantity;
            if (product.getFragile()) {
                hasFragile = true;
            }

            log.debug("Товар зарезервирован: productId={}, quantity={}, остаток={}", 
                productId, quantity, product.getQuantity());
        }

        // 5. Создаём бронирование
        OrderBooking booking = OrderBooking.builder()
                .bookingId(UUID.randomUUID())
                .orderId(orderId)
                .shoppingCartId(shoppingCartId)
                .totalWeight(totalWeight)
                .totalVolume(totalVolume)
                .fragile(hasFragile)
                .build();

        bookingRepository.save(booking);
        log.info("Создано бронирование: bookingId={}, orderId={}, weight={}, volume={}, fragile={}", 
            booking.getBookingId(), orderId, totalWeight, totalVolume, hasFragile);

        // 6. Возвращаем результат
        BookedProductsDto result = new BookedProductsDto();
        result.setDeliveryWeight(totalWeight);
        result.setDeliveryVolume(totalVolume);
        result.setFragile(hasFragile);

        log.info("Товары собраны для заказа: orderId={}, weight={}, volume={}, fragile={}", 
            orderId, totalWeight, totalVolume, hasFragile);
        
        return result;
    }

    @Override
    @Transactional
    public void shippedToDelivery(UUID orderId, UUID deliveryId) {
        log.info("Передача товаров в доставку: orderId={}, deliveryId={}", orderId, deliveryId);

        // Находим бронирование по orderId и обновляем deliveryId
        OrderBooking booking = bookingRepository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    log.error("Бронирование не найдено для заказа: {}", orderId);
                    return new ProductNotInWarehouseException(
                            "Бронирование не найдено для orderId=" + orderId);
                });

        booking.setDeliveryId(deliveryId);
        bookingRepository.save(booking);

        log.info("Доставка связана с бронированием: orderId={}, deliveryId={}", orderId, deliveryId);
    }

    @Override
    @Transactional
    public void returnProduct(Map<UUID, Long> products) {
        log.info("Возврат товаров на склад: количество позиций={}", products.size());

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();

            WarehouseProduct product = repository.findById(productId)
                    .orElseThrow(() -> new ProductNotInWarehouseException(
                            "Товар с productId=" + productId + " не найден на складе"));

            product.setQuantity(product.getQuantity() + quantity);
            repository.save(product);

            log.info("Товар возвращён на склад: productId={}, quantity={}", productId, quantity);
        }

        log.info("Возврат товаров завершён");
    }
}
