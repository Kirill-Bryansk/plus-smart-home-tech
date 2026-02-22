package ru.yandex.practicum.model.dto.shoppingcart;

import lombok.Data;

import java.util.UUID;

@Data
public class ChangeProductQuantityRequest {
    private UUID productId;
    private Long newQuantity;
}
