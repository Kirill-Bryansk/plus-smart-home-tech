package ru.yandex.practicum.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.delivery.model.Delivery;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для управления сущностями доставки.
 */
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {

    /**
     * Найти доставку по идентификатору заказа.
     */
    Optional<Delivery> findByOrderId(UUID orderId);
}
