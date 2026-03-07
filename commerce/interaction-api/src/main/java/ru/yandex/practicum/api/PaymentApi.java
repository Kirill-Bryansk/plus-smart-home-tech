package ru.yandex.practicum.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.dto.order.OrderDto;
import ru.yandex.practicum.model.dto.payment.PaymentDto;

import java.util.UUID;

/**
 * Feign-клиент для сервиса Payment.
 * Используется сервисом Order для расчёта стоимости и оплаты.
 */
@FeignClient(name = "payment")
public interface PaymentApi {

    /**
     * Рассчитать стоимость товаров в заказе.
     * Умножает количество каждого товара на цену из shopping-store.
     */
    @PostMapping("/api/v1/payment/productCost")
    ResponseEntity<Double> productCost(@RequestBody OrderDto orderDto);

    /**
     * Рассчитать общую стоимость заказа (товары + доставка + НДС).
     */
    @PostMapping("/api/v1/payment/totalCost")
    ResponseEntity<Double> getTotalCost(@RequestBody OrderDto orderDto);

    /**
     * Подготовить и сформировать сведения по оплате (сохранить в БД).
     */
    @PostMapping("/api/v1/payment")
    ResponseEntity<PaymentDto> payment(@RequestBody OrderDto orderDto);

    /**
     * Проставить признак успешной оплаты (вызов из платёжного шлюза).
     */
    @PostMapping("/api/v1/payment/refund")
    ResponseEntity<PaymentDto> paymentSuccess(@RequestBody UUID paymentId);

    /**
     * Проставить признак отказа в оплате (вызов из платёжного шлюза).
     */
    @PostMapping("/api/v1/payment/failed")
    ResponseEntity<PaymentDto> paymentFailed(@RequestBody UUID paymentId);
}
