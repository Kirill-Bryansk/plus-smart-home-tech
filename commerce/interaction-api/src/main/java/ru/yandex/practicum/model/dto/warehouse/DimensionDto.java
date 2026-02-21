package ru.yandex.practicum.model.dto.warehouse;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Размеры товара.
 */
@Data
public class DimensionDto {

    @NotNull(message = "Ширина не должна быть пустой")
    @Min(value = 1, message = "Ширина должна быть не меньше 1")
    private Double width;

    @NotNull(message = "Высота не должна быть пустой")
    @Min(value = 1, message = "Высота должна быть не меньше 1")
    private Double height;

    @NotNull(message = "Глубина не должна быть пустой")
    @Min(value = 1, message = "Глубина должна быть не меньше 1")
    private Double depth;
}
