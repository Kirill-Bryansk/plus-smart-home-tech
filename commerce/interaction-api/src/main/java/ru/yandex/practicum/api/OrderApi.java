package ru.yandex.practicum.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.model.dto.order.OrderDto;
import ru.yandex.practicum.model.dto.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

/**
 * Feign-клиент для сервиса Order.
 * Используется сервисами Delivery и Payment для уведомления об изменениях статуса.
 */
@FeignClient(name = "order")
public interface OrderApi {

    /**
     * Получить заказы пользователя.
     */
    @GetMapping("/api/v1/order")
    ResponseEntity<List<OrderDto>> getClientOrders(@RequestParam String username);

    /**
     * Создать новый заказ.
     */
    @PutMapping("/api/v1/order")
    ResponseEntity<OrderDto> createNewOrder(@RequestBody CreateNewOrderRequest request);

    /**
     * Оплата заказа (вызов из Payment).
     */
    @PostMapping("/api/v1/order/payment")
    ResponseEntity<OrderDto> payment(@RequestBody UUID orderId);

    /**
     * Оплата заказа прошла с ошибкой (вызов из Payment).
     */
    @PostMapping("/api/v1/order/payment/failed")
    ResponseEntity<OrderDto> paymentFailed(@RequestBody UUID orderId);

    /**
     * Доставка заказа (вызов из Delivery).
     */
    @PostMapping("/api/v1/order/delivery")
    ResponseEntity<OrderDto> delivery(@RequestBody UUID orderId);

    /**
     * Доставка заказа прошла с ошибкой (вызов из Delivery).
     */
    @PostMapping("/api/v1/order/delivery/failed")
    ResponseEntity<OrderDto> deliveryFailed(@RequestBody UUID orderId);

    /**
     * Завершение заказа.
     */
    @PostMapping("/api/v1/order/completed")
    ResponseEntity<OrderDto> complete(@RequestBody UUID orderId);

    /**
     * Сборка заказа.
     */
    @PostMapping("/api/v1/order/assembly")
    ResponseEntity<OrderDto> assembly(@RequestBody UUID orderId);

    /**
     * Сборка заказа прошла с ошибкой.
     */
    @PostMapping("/api/v1/order/assembly/failed")
    ResponseEntity<OrderDto> assemblyFailed(@RequestBody UUID orderId);

    /**
     * Возврат заказа.
     */
    @PostMapping("/api/v1/order/return")
    ResponseEntity<OrderDto> productReturn(@RequestBody ProductReturnRequest request);

    /**
     * Расчёт общей стоимости заказа.
     */
    @PostMapping("/api/v1/order/calculate/total")
    ResponseEntity<OrderDto> calculateTotalCost(@RequestBody UUID orderId);

    /**
     * Расчёт стоимости доставки.
     */
    @PostMapping("/api/v1/order/calculate/delivery")
    ResponseEntity<OrderDto> calculateDeliveryCost(@RequestBody UUID orderId);
}
