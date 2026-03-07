package ru.yandex.practicum.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.DeliveryApi;
import ru.yandex.practicum.api.PaymentApi;
import ru.yandex.practicum.api.WarehouseApi;
import ru.yandex.practicum.model.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.model.dto.order.OrderDto;
import ru.yandex.practicum.model.dto.order.ProductReturnRequest;
import ru.yandex.practicum.model.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.model.enums.OrderStatus;
import ru.yandex.practicum.order.exception.NoOrderFoundException;
import ru.yandex.practicum.order.mapper.OrderMapper;
import ru.yandex.practicum.order.model.Order;
import ru.yandex.practicum.order.model.OrderItem;
import ru.yandex.practicum.order.repository.OrderRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для управления заказами.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WarehouseApi warehouseApi;
    private final DeliveryApi deliveryApi;
    private final PaymentApi paymentApi;

    /**
     * Получить заказы пользователя.
     */
    @Transactional(readOnly = true)
    public List<OrderDto> getClientOrders(String username) {
        log.info("Получение заказов для пользователя: {}", username);
        // В упрощённой версии возвращаем все заказы
        // В реальности нужна связь User → Orders
        return orderRepository.findAll().stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Создать новый заказ.
     */
    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        log.info("Создание нового заказа для корзины: {}", request.getShoppingCart().getShoppingCartId());

        // 1. Собрать товары на складе
        BookedProductsDto bookedProducts = warehouseApi.assemblyProductForOrderFromShoppingCart(
                request.getShoppingCart().getShoppingCartId(),
                null // orderId будет создан ниже
        ).getBody();

        // 2. Создать заказ в БД
        Order order = new Order();
        order.setShoppingCartId(request.getShoppingCart().getShoppingCartId());
        order.setState(OrderStatus.NEW);
        
        // 3. Установить параметры из забронированных товаров
        if (bookedProducts != null) {
            order.setDeliveryWeight(bookedProducts.getDeliveryWeight());
            order.setDeliveryVolume(bookedProducts.getDeliveryVolume());
            order.setFragile(bookedProducts.getFragile());
        }

        // 4. Добавить товары из корзины в заказ
        if (request.getShoppingCart().getProducts() != null) {
            for (Map.Entry<UUID, Long> entry : request.getShoppingCart().getProducts().entrySet()) {
                OrderItem item = new OrderItem();
                item.setProductId(entry.getKey());
                item.setQuantity(entry.getValue());
                order.addItem(item);
            }
        }

        Order savedOrder = orderRepository.save(order);
        log.info("Заказ создан: {}", savedOrder.getOrderId());

        // 5. Рассчитать стоимость доставки
        calculateDeliveryCost(savedOrder.getOrderId());

        // 6. Рассчитать общую стоимость
        calculateTotalCost(savedOrder.getOrderId());

        return orderMapper.toDto(savedOrder);
    }

    /**
     * Оплата заказа.
     */
    @Transactional
    public OrderDto payment(UUID orderId) {
        log.info("Оплата заказа: {}", orderId);
        Order order = getOrderById(orderId);
        order.setState(OrderStatus.ON_PAYMENT);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    /**
     * Оплата прошла с ошибкой.
     */
    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        log.info("Ошибка оплаты заказа: {}", orderId);
        Order order = getOrderById(orderId);
        order.setState(OrderStatus.PAYMENT_FAILED);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    /**
     * Доставка заказа.
     */
    @Transactional
    public OrderDto delivery(UUID orderId) {
        log.info("Доставка заказа: {}", orderId);
        Order order = getOrderById(orderId);
        order.setState(OrderStatus.DELIVERED);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    /**
     * Доставка прошла с ошибкой.
     */
    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("Ошибка доставки заказа: {}", orderId);
        Order order = getOrderById(orderId);
        order.setState(OrderStatus.DELIVERY_FAILED);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    /**
     * Завершение заказа.
     */
    @Transactional
    public OrderDto complete(UUID orderId) {
        log.info("Завершение заказа: {}", orderId);
        Order order = getOrderById(orderId);
        order.setState(OrderStatus.COMPLETED);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    /**
     * Сборка заказа.
     */
    @Transactional
    public OrderDto assembly(UUID orderId) {
        log.info("Сборка заказа: {}", orderId);
        Order order = getOrderById(orderId);
        order.setState(OrderStatus.ASSEMBLED);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    /**
     * Сборка прошла с ошибкой.
     */
    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("Ошибка сборки заказа: {}", orderId);
        Order order = getOrderById(orderId);
        order.setState(OrderStatus.ASSEMBLY_FAILED);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    /**
     * Возврат заказа.
     */
    @Transactional
    public OrderDto productReturn(ProductReturnRequest request) {
        log.info("Возврат заказа: {}", request.getOrderId());
        Order order = getOrderById(request.getOrderId());
        order.setState(OrderStatus.PRODUCT_RETURNED);

        // Вернуть товары на склад
        warehouseApi.returnProduct(request.getProducts());

        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    /**
     * Расчёт общей стоимости заказа.
     */
    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("Расчёт общей стоимости заказа: {}", orderId);
        Order order = getOrderById(orderId);

        // Вызвать payment service для расчёта
        var response = paymentApi.getTotalCost(orderId);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            order.setTotalPrice(response.getBody());
            orderRepository.save(order);
        }

        return orderMapper.toDto(order);
    }

    /**
     * Расчёт стоимости доставки.
     */
    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("Расчёт стоимости доставки заказа: {}", orderId);
        Order order = getOrderById(orderId);

        // Вызвать delivery service для расчёта
        var response = deliveryApi.deliveryCost(orderMapper.toDto(order));
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            order.setDeliveryPrice(response.getBody());
            orderRepository.save(order);
        }

        return orderMapper.toDto(order);
    }

    /**
     * Получить заказ по ID.
     */
    private Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Заказ не найден: {}", orderId);
                    return new NoOrderFoundException("Заказ не найден: " + orderId);
                });
    }
}
