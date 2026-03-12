package ru.yandex.practicum.delivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.yandex.practicum.delivery.model.Address;
import ru.yandex.practicum.delivery.model.Delivery;
import ru.yandex.practicum.model.dto.delivery.DeliveryDto;
import ru.yandex.practicum.model.dto.warehouse.AddressDto;

/**
 * Маппер для конвертации между Delivery Entity и DeliveryDto.
 */
@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    /**
     * Конвертировать Delivery Entity в DeliveryDto.
     */
    DeliveryDto toDto(Delivery delivery);

    /**
     * Конвертировать DeliveryDto в Delivery Entity.
     */
    Delivery toEntity(DeliveryDto deliveryDto);

    /**
     * Обновить существующий Delivery Entity из DeliveryDto.
     */
    void updateEntityFromDto(DeliveryDto deliveryDto, @MappingTarget Delivery delivery);

    /**
     * Конвертировать Address в AddressDto.
     */
    AddressDto toAddressDto(Address address);

    /**
     * Конвертировать AddressDto в Address.
     */
    Address toAddress(AddressDto addressDto);
}
