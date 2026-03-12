package ru.yandex.practicum.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.OrderApi;
import ru.yandex.practicum.api.WarehouseApi;
import ru.yandex.practicum.delivery.exception.NoDeliveryFoundException;
import ru.yandex.practicum.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.delivery.model.Delivery;
import ru.yandex.practicum.delivery.repository.DeliveryRepository;
import ru.yandex.practicum.model.dto.delivery.DeliveryDto;
import ru.yandex.practicum.model.dto.order.OrderDto;
import ru.yandex.practicum.model.dto.warehouse.AddressDto;
import ru.yandex.practicum.model.enums.DeliveryStatus;
import ru.yandex.practicum.util.MathUtils;

import java.util.UUID;

/**
 * Реализация сервиса доставки.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderApi orderApi;
    private final WarehouseApi warehouseApi;

    /**
     * Базовая стоимость доставки.
     */
    private static final double BASE_COST = 5.0;

    @Override
    @Transactional
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        log.info("Создание новой доставки для заказа: {}", deliveryDto.getOrderId());

        Delivery delivery = deliveryMapper.toEntity(deliveryDto);
        delivery.setDeliveryId(UUID.randomUUID());
        delivery.setStatus(DeliveryStatus.CREATED);
        delivery.setCost(0.0); // Стоимость будет рассчитана отдельно

        Delivery saved = deliveryRepository.save(delivery);
        log.info("Доставка создана: {}", saved.getDeliveryId());

        return deliveryMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Double deliveryCost(OrderDto orderDto) {
        log.info("Расчёт стоимости доставки для заказа: {}", orderDto.getOrderId());

        // Получить адрес склада
        AddressDto warehouseAddress = warehouseApi.getWarehouseAddress().getBody();
        if (warehouseAddress == null) {
            log.warn("Не удалось получить адрес склада");
            warehouseAddress = new AddressDto();
            warehouseAddress.setStreet("ADDRESS_1");
        }

        // Адрес доставки - берём из заказа (в реальном сценарии нужно хранить в delivery)
        // Для расчёта используем параметры заказа
        Double weight = orderDto.getDeliveryWeight() != null ? orderDto.getDeliveryWeight() : 0.0;
        Double volume = orderDto.getDeliveryVolume() != null ? orderDto.getDeliveryVolume() : 0.0;
        Boolean fragile = orderDto.getFragile() != null ? orderDto.getFragile() : false;

        // Рассчитать стоимость по алгоритму из ТЗ
        Double cost = calculateDeliveryCost(warehouseAddress, weight, volume, fragile);
        log.info("Стоимость доставки для заказа {}: {}", orderDto.getOrderId(), cost);

        return cost;
    }

    /**
     * Алгоритм расчёта стоимости доставки из ТЗ.
     */
    private Double calculateDeliveryCost(AddressDto warehouseAddress, Double weight, Double volume, Boolean fragile) {
        double cost = BASE_COST;

        // 1. Умножаем на коэффициент адреса склада
        String warehouseStreet = warehouseAddress.getStreet() != null ? warehouseAddress.getStreet() : "";
        if (warehouseStreet.contains("ADDRESS_2")) {
            cost = cost * 2; // 5 * 2 = 10
        }
        // Складываем с базовой стоимостью
        cost = cost + BASE_COST; // 10 + 5 = 15 или 5 + 5 = 10

        // 2. Если хрупкий, умножаем на 0.2 и прибавляем
        if (fragile != null && fragile) {
            cost = cost + (cost * 0.2); // 15 + 3 = 18
        }

        // 3. Добавляем вес * 0.3
        cost = cost + (weight * 0.3); // 18 + (10 * 0.3) = 21

        // 4. Добавляем объём * 0.2
        cost = cost + (volume * 0.2); // 21 + (10 * 0.2) = 23

        // 5. Если улица доставки не совпадает со складом, умножаем на 0.2 и прибавляем
        // В упрощённой версии считаем, что не совпадает
        cost = cost + (cost * 0.2); // 23 + 4.6 = 27.6

        return MathUtils.round(cost);
    }

    @Override
    @Transactional
    public DeliveryDto acceptToDelivery(UUID orderId) {
        log.info("Принятие товаров в доставку для заказа: {}", orderId);

        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setStatus(DeliveryStatus.IN_PROGRESS);
        deliveryRepository.save(delivery);

        // Вызвать warehouse.shippedToDelivery
        try {
            warehouseApi.shippedToDelivery(orderId, delivery.getDeliveryId());
            log.info("Товары переданы в доставку на складе");
        } catch (Exception e) {
            log.error("Ошибка при вызове warehouse.shippedToDelivery: {}", e.getMessage());
        }

        // Изменить статус заказа на ASSEMBLED
        try {
            orderApi.assembly(orderId);
            log.info("Статус заказа {} изменён на ASSEMBLED", orderId);
        } catch (Exception e) {
            log.error("Ошибка при изменении статуса заказа: {}", e.getMessage());
        }

        return deliveryMapper.toDto(delivery);
    }

    @Override
    @Transactional
    public DeliveryDto deliverySuccess(UUID orderId) {
        log.info("Успешная доставка для заказа: {}", orderId);

        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setStatus(DeliveryStatus.DELIVERED);
        deliveryRepository.save(delivery);

        // Изменить статус заказа на DELIVERED
        try {
            orderApi.delivery(orderId);
            log.info("Статус заказа {} изменён на DELIVERED", orderId);
        } catch (Exception e) {
            log.error("Ошибка при изменении статуса заказа: {}", e.getMessage());
        }

        return deliveryMapper.toDto(delivery);
    }

    @Override
    @Transactional
    public DeliveryDto deliveryFailed(UUID orderId) {
        log.info("Ошибка доставки для заказа: {}", orderId);

        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setStatus(DeliveryStatus.FAILED);
        deliveryRepository.save(delivery);

        // Изменить статус заказа на DELIVERY_FAILED
        try {
            orderApi.deliveryFailed(orderId);
            log.info("Статус заказа {} изменён на DELIVERY_FAILED", orderId);
        } catch (Exception e) {
            log.error("Ошибка при изменении статуса заказа: {}", e.getMessage());
        }

        return deliveryMapper.toDto(delivery);
    }

    /**
     * Получить доставку по идентификатору заказа.
     */
    private Delivery getDeliveryByOrderId(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    log.error("Доставка не найдена для заказа: {}", orderId);
                    return new NoDeliveryFoundException("Доставка не найдена для заказа: " + orderId);
                });
    }
}
