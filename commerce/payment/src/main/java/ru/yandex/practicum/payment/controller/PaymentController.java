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
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;

    /**
     * Рассчитать стоимость товаров в заказе.
     */
    @Override
    @PostMapping("/productCost")
    public ResponseEntity<Double> productCost(@RequestBody OrderDto orderDto) {
        log.info("POST /productCost - расчёт стоимости товаров для заказа: {}", orderDto.getOrderId());
        Double cost = paymentService.productCost(orderDto);
        return ResponseEntity.ok(cost);
    }

    /**
     * Рассчитать общую стоимость заказа.
     */
    @Override
    @PostMapping("/totalCost")
    public ResponseEntity<Double> getTotalCost(@RequestBody OrderDto orderDto) {
        log.info("POST /totalCost - расчёт общей стоимости для заказа: {}", orderDto.getOrderId());
        Double totalCost = paymentService.getTotalCost(orderDto);
        return ResponseEntity.ok(totalCost);
    }

    /**
     * Сформировать оплату для заказа.
     */
    @Override
    @PostMapping
    public ResponseEntity<PaymentDto> payment(@RequestBody OrderDto orderDto) {
        log.info("POST / - формирование оплаты для заказа: {}", orderDto.getOrderId());
        PaymentDto result = paymentService.payment(orderDto);
        return ResponseEntity.ok(result);
    }

    /**
     * Успешная оплата (вызов из платёжного шлюза).
     */
    @Override
    @PostMapping("/refund")
    public ResponseEntity<PaymentDto> paymentSuccess(@RequestBody UUID paymentId) {
        log.info("POST /refund - успешная оплата: {}", paymentId);
        PaymentDto result = paymentService.paymentSuccess(paymentId);
        return ResponseEntity.ok(result);
    }

    /**
     * Ошибка оплаты (вызов из платёжного шлюза).
     */
    @Override
    @PostMapping("/failed")
    public ResponseEntity<PaymentDto> paymentFailed(@RequestBody UUID paymentId) {
        log.info("POST /failed - ошибка оплаты: {}", paymentId);
        PaymentDto result = paymentService.paymentFailed(paymentId);
        return ResponseEntity.ok(result);
    }
}
