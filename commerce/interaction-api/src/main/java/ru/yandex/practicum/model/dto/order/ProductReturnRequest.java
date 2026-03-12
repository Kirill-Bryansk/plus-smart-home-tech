package ru.yandex.practicum.model.dto.order;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

/**
 * Запрос на возврат товаров из заказа.
 */
@Data
public class ProductReturnRequest {

    /** Идентификатор заказа */
    private UUID orderId;

    /** Отображение идентификатора товара на количество для возврата */
    @NotEmpty(message = "Список товаров для возврата не должен быть пустым")
    private Map<UUID, Long> products;
}
