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
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryApi {

    private final DeliveryService deliveryService;

    /**
     * Создать новую доставку в БД.
     */
    @Override
    @PutMapping
    public ResponseEntity<DeliveryDto> planDelivery(@RequestBody DeliveryDto deliveryDto) {
        log.info("PUT / - создание доставки для заказа: {}", deliveryDto.getOrderId());
        DeliveryDto result = deliveryService.planDelivery(deliveryDto);
        return ResponseEntity.ok(result);
    }

    /**
     * Рассчитать стоимость доставки.
     */
    @Override
    @PostMapping("/cost")
    public ResponseEntity<Double> deliveryCost(@RequestBody OrderDto orderDto) {
        log.info("POST /cost - расчёт стоимости для заказа: {}", orderDto.getOrderId());
        Double cost = deliveryService.deliveryCost(orderDto);
        return ResponseEntity.ok(cost);
    }

    /**
     * Принять товары в доставку (изменить статус на IN_PROGRESS).
     */
    @Override
    @PostMapping("/in-progress")
    public ResponseEntity<DeliveryDto> acceptToDelivery(@RequestBody UUID orderId) {
        log.info("POST /in-progress - принятие товаров в доставку для заказа: {}", orderId);
        DeliveryDto result = deliveryService.acceptToDelivery(orderId);
        return ResponseEntity.ok(result);
    }

    /**
     * Успешная доставка.
     */
    @Override
    @PostMapping("/success")
    public ResponseEntity<DeliveryDto> deliverySuccess(@RequestBody UUID orderId) {
        log.info("POST /success - успешная доставка для заказа: {}", orderId);
        DeliveryDto result = deliveryService.deliverySuccess(orderId);
        return ResponseEntity.ok(result);
    }

    /**
     * Ошибка доставки.
     */
    @Override
    @PostMapping("/failed")
    public ResponseEntity<DeliveryDto> deliveryFailed(@RequestBody UUID orderId) {
        log.info("POST /failed - ошибка доставки для заказа: {}", orderId);
        DeliveryDto result = deliveryService.deliveryFailed(orderId);
        return ResponseEntity.ok(result);
    }
}
