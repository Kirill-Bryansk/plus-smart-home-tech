package ru.yandex.practicum.order.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.model.dto.order.OrderDto;
import ru.yandex.practicum.order.model.Order;
import ru.yandex.practicum.order.model.OrderItem;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Маппер для конвертации Order Entity ↔ OrderDto.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "products", ignore = true)
    OrderDto toDto(Order order);

    Order toEntity(OrderDto orderDto);

    /**
     * Преобразовать список товаров заказа в Map<productId, quantity>.
     */
    default Map<java.util.UUID, Long> toProductsMap(java.util.List<OrderItem> items) {
        if (items == null) {
            return null;
        }
        return items.stream()
                .collect(Collectors.toMap(
                        OrderItem::getProductId,
                        OrderItem::getQuantity
                ));
    }

    /**
     * Заполнить DTO товарами из сущности.
     */
    @AfterMapping
    default void fillProducts(@MappingTarget OrderDto dto, Order order) {
        if (order.getItems() != null) {
            dto.setProducts(toProductsMap(order.getItems()));
        }
    }
}
