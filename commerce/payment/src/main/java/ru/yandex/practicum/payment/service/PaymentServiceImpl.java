package ru.yandex.practicum.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.OrderApi;
import ru.yandex.practicum.api.ShoppingStoreApi;
import ru.yandex.practicum.model.dto.order.OrderDto;
import ru.yandex.practicum.model.dto.payment.PaymentDto;
import ru.yandex.practicum.model.dto.store.ProductDto;
import ru.yandex.practicum.model.enums.PaymentStatus;
import ru.yandex.practicum.payment.exception.NoPaymentFoundException;
import ru.yandex.practicum.payment.mapper.PaymentMapper;
import ru.yandex.practicum.payment.model.Payment;
import ru.yandex.practicum.payment.repository.PaymentRepository;

import java.util.Map;
import java.util.UUID;

/**
 * Реализация сервиса оплаты.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ShoppingStoreApi shoppingStoreApi;
    private final OrderApi orderApi;

    @Override
    @Transactional(readOnly = true)
    public Double productCost(OrderDto orderDto) {
        log.info("Расчёт стоимости товаров для заказа: {}", orderDto.getOrderId());

        if (orderDto.getProducts() == null || orderDto.getProducts().isEmpty()) {
            log.warn("Товары в заказе не указаны");
            return 0.0;
        }

        double totalProductCost = 0.0;

        // Для каждого товара в заказе получаем цену из shopping-store
        for (Map.Entry<UUID, Long> entry : orderDto.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();

            try {
                ProductDto product = shoppingStoreApi.getProduct(productId).getBody();
                if (product != null && product.getPrice() != null) {
                    double price = product.getPrice().doubleValue();
                    totalProductCost += price * quantity;
                    log.debug("Товар {}: цена={}, количество={}, сумма={}", 
                        productId, price, quantity, price * quantity);
                }
            } catch (Exception e) {
                log.error("Ошибка получения цены для товара {}: {}", productId, e.getMessage());
            }
        }

        log.info("Общая стоимость товаров для заказа {}: {}", orderDto.getOrderId(), totalProductCost);
        return totalProductCost;
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalCost(OrderDto orderDto) {
        log.info("Расчёт общей стоимости заказа: {}", orderDto.getOrderId());

        // 1. Рассчитать стоимость товаров
        Double productCost = productCost(orderDto);

        // 2. Рассчитать НДС (10% от стоимости товаров)
        Double vat = productCost * 0.1;

        // 3. Получить стоимость доставки из заказа
        Double deliveryCost = orderDto.getDeliveryPrice() != null ? orderDto.getDeliveryPrice() : 0.0;

        // 4. Общая стоимость = товары + НДС + доставка
        Double totalCost = productCost + vat + deliveryCost;

        log.info("Общая стоимость заказа {}: товары={}, НДС={}, доставка={}, итого={}", 
            orderDto.getOrderId(), productCost, vat, deliveryCost, totalCost);

        return totalCost;
    }

    @Override
    @Transactional
    public PaymentDto payment(OrderDto orderDto) {
        log.info("Формирование оплаты для заказа: {}", orderDto.getOrderId());

        // 1. Рассчитать стоимость товаров
        Double productCost = productCost(orderDto);

        // 2. Рассчитать общую стоимость
        Double totalCost = getTotalCost(orderDto);

        // 3. Получить стоимость доставки
        Double deliveryCost = orderDto.getDeliveryPrice() != null ? orderDto.getDeliveryPrice() : 0.0;

        // 4. Создать сущность оплаты
        Payment payment = Payment.builder()
                .paymentId(UUID.randomUUID())
                .orderId(orderDto.getOrderId())
                .productCost(productCost)
                .deliveryCost(deliveryCost)
                .totalCost(totalCost)
                .status(PaymentStatus.PENDING)
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Оплата создана: {}", saved.getPaymentId());

        return paymentMapper.toDto(saved);
    }

    @Override
    @Transactional
    public PaymentDto paymentSuccess(UUID paymentId) {
        log.info("Успешная оплата: {}", paymentId);

        Payment payment = getPaymentById(paymentId);
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        // Изменить статус заказа на PAID
        try {
            orderApi.payment(payment.getOrderId());
            log.info("Статус заказа {} изменён на PAID", payment.getOrderId());
        } catch (Exception e) {
            log.error("Ошибка при изменении статуса заказа: {}", e.getMessage());
        }

        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional
    public PaymentDto paymentFailed(UUID paymentId) {
        log.info("Ошибка оплаты: {}", paymentId);

        Payment payment = getPaymentById(paymentId);
        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);

        // Изменить статус заказа на PAYMENT_FAILED
        try {
            orderApi.paymentFailed(payment.getOrderId());
            log.info("Статус заказа {} изменён на PAYMENT_FAILED", payment.getOrderId());
        } catch (Exception e) {
            log.error("Ошибка при изменении статуса заказа: {}", e.getMessage());
        }

        return paymentMapper.toDto(payment);
    }

    /**
     * Получить оплату по идентификатору.
     */
    private Payment getPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    log.error("Оплата не найдена: {}", paymentId);
                    return new NoPaymentFoundException("Оплата не найдена: " + paymentId);
                });
    }
}
