package ru.yandex.practicum.model.dto.warehouse;

import lombok.Data;

/**
 * Представление адреса в системе.
 */
@Data
public class AddressDto {

    private String country;

    private String city;

    private String street;

    private String house;

    private String flat;
}
