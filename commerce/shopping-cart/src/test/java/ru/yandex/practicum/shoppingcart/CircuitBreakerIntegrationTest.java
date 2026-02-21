package ru.yandex.practicum.shoppingcart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.model.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.model.dto.warehouse.ShoppingCartDto;
import ru.yandex.practicum.shoppingcart.client.WarehouseClient;
import ru.yandex.practicum.shoppingcart.service.ShoppingCartService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Интеграционный тест для проверки Circuit Breaker.
 */
@SpringBootTest
@DisplayName("Тест Circuit Breaker для Warehouse Client")
class CircuitBreakerIntegrationTest {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @MockBean
    private WarehouseClient warehouseClient;

    private String testUsername;
    private Map<UUID, Long> testProducts;

    @BeforeEach
    void setUp() {
        testUsername = "test-user-" + System.currentTimeMillis();
        testProducts = new HashMap<>();
        testProducts.put(UUID.randomUUID(), 1L);
    }

    @Test
    @DisplayName("1. Нормальная работа - Circuit Breaker в состоянии CLOSED")
    void testCircuitBreakerClosed_State() {
        // Arrange: warehouse доступен
        BookedProductsDto mockResponse = new BookedProductsDto();
        mockResponse.setDeliveryWeight(10.0);
        mockResponse.setDeliveryVolume(5.0);
        mockResponse.setFragile(false);

        when(warehouseClient.checkProductQuantityInWarehouse(any(ShoppingCartDto.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        // Act: добавляем товар в корзину
        ShoppingCartDto result = shoppingCartService.addProductToCart(testUsername, testProducts);

        // Assert: операция успешна, warehouse вызван
        assertNotNull(result);
        assertNotNull(result.getShoppingCartId());
        assertEquals(1, result.getProducts().size());
        verify(warehouseClient, times(1)).checkProductQuantityInWarehouse(any(ShoppingCartDto.class));
    }

    @Test
    @DisplayName("2. Circuit Breaker срабатывает - вызывается fallback метод")
    void testCircuitBreakerOpen_FallbackCalled() {
        // Arrange: warehouse недоступен (выбрасывает исключение)
        when(warehouseClient.checkProductQuantityInWarehouse(any(ShoppingCartDto.class)))
                .thenThrow(new RuntimeException("Warehouse недоступен"));

        // Act: добавляем товар в корзину (должен сработать fallback)
        ShoppingCartDto result = shoppingCartService.addProductToCart(testUsername, testProducts);

        // Assert: операция успешна благодаря fallback
        assertNotNull(result);
        assertNotNull(result.getShoppingCartId());
        assertEquals(1, result.getProducts().size());
        
        // warehouse вызван 1 раз (упал с ошибкой)
        verify(warehouseClient, times(1)).checkProductQuantityInWarehouse(any(ShoppingCartDto.class));
    }

    @Test
    @DisplayName("3. Multiple вызовы при открытом Circuit Breaker")
    void testMultipleCallsWithOpenCircuitBreaker() {
        // Arrange: warehouse недоступен
        when(warehouseClient.checkProductQuantityInWarehouse(any(ShoppingCartDto.class)))
                .thenThrow(new RuntimeException("Warehouse недоступен"));

        // Act: несколько вызовов с разными пользователями
        for (int i = 0; i < 5; i++) {
            String username = "test-user-" + i;
            Map<UUID, Long> products = new HashMap<>();
            products.put(UUID.randomUUID(), 1L);
            
            ShoppingCartDto result = shoppingCartService.addProductToCart(username, products);
            
            // Assert: каждый вызов успешен благодаря fallback
            assertNotNull(result);
            assertNotNull(result.getShoppingCartId());
        }

        // warehouse вызван 5 раз (каждый раз падает)
        verify(warehouseClient, times(5)).checkProductQuantityInWarehouse(any(ShoppingCartDto.class));
    }

    @Test
    @DisplayName("4. Проверка работы fallback при множественных ошибках")
    void testFallbackAfterMultipleFailures() {
        // Arrange: warehouse недоступен
        when(warehouseClient.checkProductQuantityInWarehouse(any(ShoppingCartDto.class)))
                .thenThrow(new RuntimeException("Warehouse недоступен"));

        // Act: первый вызов (попытка вызвать warehouse)
        ShoppingCartDto result1 = shoppingCartService.addProductToCart(testUsername, testProducts);
        
        // Act: второй вызов (Circuit Breaker ещё не открыт, требуется минимум 2 вызова)
        ShoppingCartDto result2 = shoppingCartService.addProductToCart(testUsername + "-2", testProducts);

        // Assert: оба вызова успешны благодаря fallback
        assertNotNull(result1);
        assertNotNull(result2);
        
        // warehouse вызван 2 раза (каждый раз падает)
        verify(warehouseClient, times(2)).checkProductQuantityInWarehouse(any(ShoppingCartDto.class));
    }
}
