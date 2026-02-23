package ru.yandex.practicum.shoppingcart.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mapping;
import ru.yandex.practicum.model.dto.warehouse.ShoppingCartDto;
import ru.yandex.practicum.shoppingcart.model.ShoppingCart;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ShoppingCartMapper {

    @Mapping(target = "products", ignore = true)
    ShoppingCart toEntity(ShoppingCartDto dto);

    @Mapping(target = "products", ignore = true)
    ShoppingCartDto toDto(ShoppingCart entity);
}
