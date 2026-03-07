package ru.yandex.practicum.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.api.PaymentApi;
import ru.yandex.practicum.model.dto.order.OrderDto;
import ru.yandex.practicum.model.dto.payment.PaymentDto;
import ru.yandex.practicum.payment.service.PaymentService;

import java.util.UUID;

/**
 * Контроллер для управления оплатой.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;

    /**
     * Рассчитать стоимость товаров в заказе.
     */
    @Override
    @PostMapping("/api/v1/payment/productCost")
    public ResponseEntity<Double> productCost(@RequestBody OrderDto orderDto) {
        log.info("POST /api/v1/payment/productCost - расчёт стоимости товаров для заказа: {}", orderDto.getOrderId());
        Double cost = paymentService.productCost(orderDto);
        return ResponseEntity.ok(cost);
    }

    /**
     * Рассчитать общую стоимость заказа.
     */
    @Override
    @PostMapping("/api/v1/payment/totalCost")
    public ResponseEntity<Double> getTotalCost(@RequestBody OrderDto orderDto) {
        log.info("POST /api/v1/payment/totalCost - расчёт общей стоимости для заказа: {}", orderDto.getOrderId());
        Double totalCost = paymentService.getTotalCost(orderDto);
        return ResponseEntity.ok(totalCost);
    }

    /**
     * Сформировать оплату для заказа.
     */
    @Override
    @PostMapping("/api/v1/payment")
    public ResponseEntity<PaymentDto> payment(@RequestBody OrderDto orderDto) {
        log.info("POST /api/v1/payment - формирование оплаты для заказа: {}", orderDto.getOrderId());
        PaymentDto result = paymentService.payment(orderDto);
        return ResponseEntity.ok(result);
    }

    /**
     * Успешная оплата (вызов из платёжного шлюза).
     */
    @Override
    @PostMapping("/api/v1/payment/refund")
    public ResponseEntity<PaymentDto> paymentSuccess(@RequestBody UUID paymentId) {
        log.info("POST /api/v1/payment/refund - успешная оплата: {}", paymentId);
        PaymentDto result = paymentService.paymentSuccess(paymentId);
        return ResponseEntity.ok(result);
    }

    /**
     * Ошибка оплаты (вызов из платёжного шлюза).
     */
    @Override
    @PostMapping("/api/v1/payment/failed")
    public ResponseEntity<PaymentDto> paymentFailed(@RequestBody UUID paymentId) {
        log.info("POST /api/v1/payment/failed - ошибка оплаты: {}", paymentId);
        PaymentDto result = paymentService.paymentFailed(paymentId);
        return ResponseEntity.ok(result);
    }
}
