package ru.yandex.practicum.model.dto.warehouse;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Запрос на добавление нового товара на склад.
 */
@Data
public class NewProductInWarehouseRequest {

    @NotNull(message = "Идентификатор товара не должен быть пустым")
    private UUID productId;

    private Boolean fragile;

    @NotNull(message = "Размеры товара не должны быть пустыми")
    private DimensionDto dimension;

    @NotNull(message = "Вес товара не должен быть пустым")
    @Min(value = 1, message = "Вес товара должен быть не меньше 1")
    private Double weight;
}
