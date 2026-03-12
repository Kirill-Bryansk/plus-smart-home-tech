package ru.yandex.practicum.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.order.model.OrderItem;

import java.util.UUID;

/**
 * Репозиторий для работы с товарами заказа.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    /**
     * Удалить товары заказа по идентификатору заказа.
     */
    @Modifying
    @Query("DELETE FROM OrderItem oi WHERE oi.order.orderId = :orderId")
    void deleteByOrderId(@Param("orderId") UUID orderId);
}
