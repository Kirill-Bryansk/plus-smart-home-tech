package ru.yandex.practicum.model.dto.warehouse;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Запрос на увеличение количества товара на складе.
 */
@Data
public class AddProductToWarehouseRequest {

    private UUID productId;

    @NotNull(message = "Количество товара не должно быть пустым")
    @Min(value = 1, message = "Количество товара должно быть не меньше 1")
    private Long quantity;
}
