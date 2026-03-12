package ru.yandex.practicum.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.dto.delivery.DeliveryDto;
import ru.yandex.practicum.model.dto.order.OrderDto;

import java.util.UUID;

/**
 * Feign-клиент для сервиса Delivery.
 * Используется сервисом Order для расчёта и создания доставки.
 */
@FeignClient(name = "delivery")
public interface DeliveryApi {

    /**
     * Создать доставку для заказа.
     */
    @PutMapping("/api/v1/delivery")
    ResponseEntity<DeliveryDto> planDelivery(@RequestBody DeliveryDto deliveryDto);

    /**
     * Рассчитать стоимость доставки.
     */
    @PostMapping("/api/v1/delivery/cost")
    ResponseEntity<Double> deliveryCost(@RequestBody OrderDto orderDto);

    /**
     * Принять товары в доставку (изменить статус на IN_PROGRESS).
     */
    @PostMapping("/api/v1/delivery/in-progress")
    ResponseEntity<DeliveryDto> acceptToDelivery(@RequestBody UUID orderId);

    /**
     * Успешная доставка.
     */
    @PostMapping("/api/v1/delivery/success")
    ResponseEntity<DeliveryDto> deliverySuccess(@RequestBody UUID orderId);

    /**
     * Ошибка доставки.
     */
    @PostMapping("/api/v1/delivery/failed")
    ResponseEntity<DeliveryDto> deliveryFailed(@RequestBody UUID orderId);
}
