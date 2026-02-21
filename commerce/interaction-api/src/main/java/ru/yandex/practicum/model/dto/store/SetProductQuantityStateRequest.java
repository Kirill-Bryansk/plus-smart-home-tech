package ru.yandex.practicum.model.dto.store;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.model.enums.QuantityState;

import java.util.UUID;

/**
 * Запрос на изменение статуса остатка товара.
 */
@Data
public class SetProductQuantityStateRequest {

    @NotNull(message = "Идентификатор товара не должен быть пустым")
    private UUID productId;

    @NotNull(message = "Статус количества не должен быть пустым")
    private QuantityState quantityState;
}
