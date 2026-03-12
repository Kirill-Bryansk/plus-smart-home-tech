package ru.yandex.practicum.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.order.model.Order;
import ru.yandex.practicum.model.enums.OrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с заказами.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    /**
     * Найти заказы пользователя по идентификатору корзины.
     */
    List<Order> findByShoppingCartId(UUID shoppingCartId);

    /**
     * Найти заказ по статусу.
     */
    List<Order> findByState(OrderStatus state);

    /**
     * Найти заказ по идентификатору оплаты.
     */
    Optional<Order> findByPaymentId(UUID paymentId);

    /**
     * Найти заказ по идентификатору доставки.
     */
    Optional<Order> findByDeliveryId(UUID deliveryId);
}
