package ru.yandex.practicum.model.dto.store;

import lombok.Data;
import ru.yandex.practicum.model.enums.ProductCategory;
import ru.yandex.practicum.model.enums.ProductState;
import ru.yandex.practicum.model.enums.QuantityState;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Товар, продаваемый в интернет-магазине.
 */
@Data
public class ProductDto {

    @NotNull(message = "Идентификатор товара не должен быть пустым")
    private UUID productId;

    @NotBlank(message = "Название товара не должно быть пустым")
    private String productName;

    @NotBlank(message = "Описание товара не должно быть пустым")
    private String description;

    private String imageSrc;

    @NotNull(message = "Статус количества товара не должен быть пустым")
    private QuantityState quantityState;

    @NotNull(message = "Статус товара не должен быть пустым")
    private ProductState productState;

    @NotNull(message = "Категория товара не должна быть пустой")
    private ProductCategory productCategory;

    @Min(value = 1, message = "Цена товара должна быть не меньше 1")
    private BigDecimal price;
}
