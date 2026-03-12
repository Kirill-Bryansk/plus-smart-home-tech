package ru.yandex.practicum.order.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.model.dto.order.OrderDto;
import ru.yandex.practicum.model.dto.order.ProductReturnRequest;
import ru.yandex.practicum.order.exception.NotAuthorizedUserException;
import ru.yandex.practicum.order.service.OrderService;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для управления заказами.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Получить заказы пользователя.
     */
    @GetMapping
    public ResponseEntity<List<OrderDto>> getClientOrders(@RequestParam String username) {
        log.debug("Запрос на получение заказов для пользователя: {}", username);
        
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }
        
        List<OrderDto> orders = orderService.getClientOrders(username);
        return ResponseEntity.ok(orders);
    }

    /**
     * Создать новый заказ.
     */
    @PutMapping
    public ResponseEntity<OrderDto> createNewOrder(@RequestBody CreateNewOrderRequest request) {
        log.debug("Запрос на создание нового заказа");
        OrderDto order = orderService.createNewOrder(request);
        return ResponseEntity.ok(order);
    }

    /**
     * Оплата заказа.
     */
    @PostMapping("/payment")
    public ResponseEntity<OrderDto> payment(@RequestBody UUID orderId) {
        log.debug("Запрос на оплату заказа: {}", orderId);
        OrderDto order = orderService.payment(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * Оплата заказа прошла с ошибкой.
     */
    @PostMapping("/payment/failed")
    public ResponseEntity<OrderDto> paymentFailed(@RequestBody UUID orderId) {
        log.debug("Запрос на ошибку оплаты заказа: {}", orderId);
        OrderDto order = orderService.paymentFailed(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * Доставка заказа.
     */
    @PostMapping("/delivery")
    public ResponseEntity<OrderDto> delivery(@RequestBody UUID orderId) {
        log.debug("Запрос на доставку заказа: {}", orderId);
        OrderDto order = orderService.delivery(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * Доставка заказа прошла с ошибкой.
     */
    @PostMapping("/delivery/failed")
    public ResponseEntity<OrderDto> deliveryFailed(@RequestBody UUID orderId) {
        log.debug("Запрос на ошибку доставки заказа: {}", orderId);
        OrderDto order = orderService.deliveryFailed(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * Завершение заказа.
     */
    @PostMapping("/completed")
    public ResponseEntity<OrderDto> complete(@RequestBody UUID orderId) {
        log.debug("Запрос на завершение заказа: {}", orderId);
        OrderDto order = orderService.complete(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * Сборка заказа.
     */
    @PostMapping("/assembly")
    public ResponseEntity<OrderDto> assembly(@RequestBody UUID orderId) {
        log.debug("Запрос на сборку заказа: {}", orderId);
        OrderDto order = orderService.assembly(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * Сборка заказа прошла с ошибкой.
     */
    @PostMapping("/assembly/failed")
    public ResponseEntity<OrderDto> assemblyFailed(@RequestBody UUID orderId) {
        log.debug("Запрос на ошибку сборки заказа: {}", orderId);
        OrderDto order = orderService.assemblyFailed(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * Возврат заказа.
     */
    @PostMapping("/return")
    public ResponseEntity<OrderDto> productReturn(@RequestBody ProductReturnRequest request) {
        log.debug("Запрос на возврат заказа: {}", request.getOrderId());
        OrderDto order = orderService.productReturn(request);
        return ResponseEntity.ok(order);
    }

    /**
     * Расчёт общей стоимости заказа.
     */
    @PostMapping("/calculate/total")
    public ResponseEntity<OrderDto> calculateTotalCost(@RequestBody UUID orderId) {
        log.debug("Запрос на расчёт общей стоимости заказа: {}", orderId);
        OrderDto order = orderService.calculateTotalCost(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * Расчёт стоимости доставки.
     */
    @PostMapping("/calculate/delivery")
    public ResponseEntity<OrderDto> calculateDeliveryCost(@RequestBody UUID orderId) {
        log.debug("Запрос на расчёт стоимости доставки: {}", orderId);
        OrderDto order = orderService.calculateDeliveryCost(orderId);
        return ResponseEntity.ok(order);
    }
}
