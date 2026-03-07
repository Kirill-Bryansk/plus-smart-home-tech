package ru.yandex.practicum.delivery.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.api.DeliveryApi;
import ru.yandex.practicum.delivery.service.DeliveryService;
import ru.yandex.practicum.model.dto.delivery.DeliveryDto;
import ru.yandex.practicum.model.dto.order.OrderDto;

import java.util.UUID;

/**
 * Контроллер для управления доставкой.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class DeliveryController implements DeliveryApi {

    private final DeliveryService deliveryService;

    /**
     * Создать новую доставку в БД.
     */
    @Override
    @PutMapping("/api/v1/delivery")
    public ResponseEntity<DeliveryDto> planDelivery(@RequestBody DeliveryDto deliveryDto) {
        log.info("PUT /api/v1/delivery - создание доставки для заказа: {}", deliveryDto.getOrderId());
        DeliveryDto result = deliveryService.planDelivery(deliveryDto);
        return ResponseEntity.ok(result);
    }

    /**
     * Рассчитать стоимость доставки.
     */
    @Override
    @PostMapping("/api/v1/delivery/cost")
    public ResponseEntity<Double> deliveryCost(@RequestBody OrderDto orderDto) {
        log.info("POST /api/v1/delivery/cost - расчёт стоимости для заказа: {}", orderDto.getOrderId());
        Double cost = deliveryService.deliveryCost(orderDto);
        return ResponseEntity.ok(cost);
    }

    /**
     * Принять товары в доставку (изменить статус на IN_PROGRESS).
     */
    @Override
    @PostMapping("/api/v1/delivery/in-progress")
    public ResponseEntity<DeliveryDto> acceptToDelivery(@RequestBody UUID orderId) {
        log.info("POST /api/v1/delivery/in-progress - принятие товаров в доставку для заказа: {}", orderId);
        DeliveryDto result = deliveryService.acceptToDelivery(orderId);
        return ResponseEntity.ok(result);
    }

    /**
     * Успешная доставка.
     */
    @Override
    @PostMapping("/api/v1/delivery/success")
    public ResponseEntity<DeliveryDto> deliverySuccess(@RequestBody UUID orderId) {
        log.info("POST /api/v1/delivery/success - успешная доставка для заказа: {}", orderId);
        DeliveryDto result = deliveryService.deliverySuccess(orderId);
        return ResponseEntity.ok(result);
    }

    /**
     * Ошибка доставки.
     */
    @Override
    @PostMapping("/api/v1/delivery/failed")
    public ResponseEntity<DeliveryDto> deliveryFailed(@RequestBody UUID orderId) {
        log.info("POST /api/v1/delivery/failed - ошибка доставки для заказа: {}", orderId);
        DeliveryDto result = deliveryService.deliveryFailed(orderId);
        return ResponseEntity.ok(result);
    }
}
