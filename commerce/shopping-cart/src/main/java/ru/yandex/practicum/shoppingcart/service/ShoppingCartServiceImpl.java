package ru.yandex.practicum.shoppingcart.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.model.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.model.dto.warehouse.ShoppingCartDto;
import ru.yandex.practicum.shoppingcart.client.WarehouseClient;
import ru.yandex.practicum.shoppingcart.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.shoppingcart.exception.ShoppingCartNotFoundException;
import ru.yandex.practicum.shoppingcart.mapper.ShoppingCartMapper;
import ru.yandex.practicum.shoppingcart.model.CartProduct;
import ru.yandex.practicum.shoppingcart.model.ShoppingCart;
import ru.yandex.practicum.shoppingcart.repository.ShoppingCartRepository;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository repository;
    private final ShoppingCartMapper mapper;
    private final WarehouseClient warehouseClient;

    @Transactional(readOnly = true)
    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        log.info("Получение корзины пользователя: {}", username);
        
        ShoppingCart cart = repository.findByUsername(username)
                .orElseGet(() -> {
                    log.info("Корзина не найдена, создаём новую для: {}", username);
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUsername(username);
                    return repository.save(newCart);
                });

        ShoppingCartDto dto = mapper.toDto(cart);
        dto.setProducts(convertToMap(cart.getProducts()));
        
        log.info("Корзина получена: cartId={}, товаров: {}", dto.getShoppingCartId(), dto.getProducts().size());
        return dto;
    }

    @Transactional
    @CircuitBreaker(name = "warehouseClient", fallbackMethod = "addProductToCartFallback")
    @Override
    public ShoppingCartDto addProductToCart(String username, Map<UUID, Long> products) {
        log.info("Добавление товаров в корзину пользователя: {}, товары: {}", username, products);

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Имя пользователя не должно быть пустым");
        }

        ShoppingCart cart = repository.findByUsername(username)
                .orElseGet(() -> {
                    log.info("Корзина не найдена, создаем новую для пользователя: {}", username);
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUsername(username);
                    return repository.save(newCart);
                });
        log.info("Корзина найдена/создана: cartId={}", cart.getShoppingCartId());

        // Проверяем наличие на складе через Circuit Breaker
        ShoppingCartDto cartDto = mapper.toDto(cart);
        cartDto.setProducts(convertToMap(cart.getProducts()));
        cartDto.setShoppingCartId(cart.getShoppingCartId());

        try {
            // Вызов warehouse через Circuit Breaker
            log.info("Вызов warehouse для проверки товаров: {}", products);
            BookedProductsDto booked = warehouseClient.checkProductQuantityInWarehouse(cartDto).getBody();
            log.info("Проверка на складе пройдена: weight={}, volume={}, fragile={}",
                    booked.getDeliveryWeight(), booked.getDeliveryVolume(), booked.getFragile());
            log.info("Ответ от warehouse получен");
        } catch (Exception e) {
            log.error("Ошибка при проверке склада: {}", e.getMessage());
            throw e; // или обработать через fallback
        }

        // Добавляем новые товары
        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();
            log.debug("Обработка товара: productId={}, quantity={}", productId, quantity);

            CartProduct existingProduct = cart.getProducts().stream()
                    .filter(p -> p.getProductId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (existingProduct != null) {
                existingProduct.setQuantity(existingProduct.getQuantity() + quantity);
                log.debug("Обновлено количество существующего товара: новое количество={}", existingProduct.getQuantity());
            } else {
                CartProduct newProduct = new CartProduct();
                newProduct.setShoppingCartId(cart.getShoppingCartId());
                newProduct.setProductId(productId);
                newProduct.setQuantity(quantity);
                newProduct.setShoppingCart(cart);
                cart.getProducts().add(newProduct);
                log.debug("Добавлен новый товар в корзину");
            }
        }

        repository.save(cart);
        log.info("Товары добавлены в корзину: cartId={}, всего товаров={}",
                cart.getShoppingCartId(), cart.getProducts().size());

        ShoppingCartDto result = mapper.toDto(cart);
        result.setProducts(convertToMap(cart.getProducts()));
        return result;
    }

    /**
     * Fallback метод для addProductToCart.
     * Вызывается когда Circuit Breaker открыт (warehouse недоступен).
     */
    @Transactional
    public ShoppingCartDto addProductToCartFallback(String username, Map<UUID, Long> products, Throwable t) {
        log.error("Circuit Breaker сработал! Warehouse недоступен. Используем fallback логику. Ошибка: {}",
                t.getMessage());

        // Fallback логика: добавляем товары без проверки на складе
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Имя пользователя не должно быть пустым");
        }

        ShoppingCart cart = repository.findByUsername(username)
                .orElseGet(() -> {
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUsername(username);
                    return repository.save(newCart);
                });

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();

            CartProduct existingProduct = cart.getProducts().stream()
                    .filter(p -> p.getProductId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (existingProduct != null) {
                existingProduct.setQuantity(existingProduct.getQuantity() + quantity);
            } else {
                CartProduct newProduct = new CartProduct();
                newProduct.setShoppingCartId(cart.getShoppingCartId());
                newProduct.setProductId(productId);
                newProduct.setQuantity(quantity);
                newProduct.setShoppingCart(cart);
                cart.getProducts().add(newProduct);
            }
        }

        repository.save(cart);
        log.info("Товары добавлены в корзину без проверки на складе (fallback): cartId={}", cart.getShoppingCartId());

        ShoppingCartDto result = mapper.toDto(cart);
        result.setProducts(convertToMap(cart.getProducts()));
        return result;
    }

    @Transactional
    @Override
    public void deactivateCart(String username) {
        log.info("Деактивация корзины пользователя: {}", username);
        
        ShoppingCart cart = repository.findByUsername(username)
                .orElseThrow(() -> new ShoppingCartNotFoundException(
                        "Корзина пользователя " + username + " не найдена"));
        
        cart.setCartState("INACTIVE");
        repository.save(cart);
        log.info("Корзина деактивирована: cartId={}", cart.getShoppingCartId());
    }

    @Transactional
    @Override
    public ShoppingCartDto removeFromCart(String username, List<UUID> productIds) {
        log.info("Удаление товаров из корзины: {}, товары: {}", username, productIds);
        
        ShoppingCart cart = repository.findByUsername(username)
                .orElseThrow(() -> new ShoppingCartNotFoundException(
                        "Корзина пользователя " + username + " не найдена"));
        
        cart.getProducts().removeIf(p -> productIds.contains(p.getProductId()));
        repository.save(cart);
        
        log.info("Товары удалены из корзины: cartId={}", cart.getShoppingCartId());
        
        ShoppingCartDto result = mapper.toDto(cart);
        result.setProducts(convertToMap(cart.getProducts()));
        return result;
    }

    @Transactional
    @Override
    public ShoppingCartDto changeProductQuantity(String username, UUID productId, Long newQuantity) {
        log.info("Изменение количества товара: user={}, productId={}, newQuantity={}", 
                username, productId, newQuantity);
        
        ShoppingCart cart = repository.findByUsername(username)
                .orElseThrow(() -> new ShoppingCartNotFoundException(
                        "Корзина пользователя " + username + " не найдена"));
        
        CartProduct product = cart.getProducts().stream()
                .filter(p -> p.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new NoProductsInShoppingCartException(
                        "Товар productId=" + productId + " не найден в корзине"));
        
        product.setQuantity(newQuantity);
        repository.save(cart);
        
        log.info("Количество товара изменено: cartId={}, productId={}", cart.getShoppingCartId(), productId);
        
        ShoppingCartDto result = mapper.toDto(cart);
        result.setProducts(convertToMap(cart.getProducts()));
        return result;
    }

    // Вспомогательный метод для конвертации List<CartProduct> в Map<UUID, Long>
    private Map<UUID, Long> convertToMap(List<CartProduct> products) {
        Map<UUID, Long> result = new HashMap<>();
        for (CartProduct product : products) {
            result.put(product.getProductId(), product.getQuantity());
        }
        return result;
    }
}
