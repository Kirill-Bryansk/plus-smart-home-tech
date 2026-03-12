package ru.yandex.practicum.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.warehouse.model.OrderBooking;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для управления забронированными товарами.
 */
@Repository
public interface OrderBookingRepository extends JpaRepository<OrderBooking, UUID> {

    /**
     * Найти бронирование по идентификатору заказа.
     */
    Optional<OrderBooking> findByOrderId(UUID orderId);
}
