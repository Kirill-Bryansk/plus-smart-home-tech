package ru.yandex.practicum.model.dto.warehouse;

import lombok.Data;

/**
 * Общие сведения о зарезервированных товарах по корзине.
 */
@Data
public class BookedProductsDto {

    private Double deliveryWeight;

    private Double deliveryVolume;

    private Boolean fragile;
}
