package ru.yandex.practicum.warehouse.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.model.dto.warehouse.*;
import ru.yandex.practicum.warehouse.model.WarehouseProduct;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WarehouseMapper {

    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "width", source = "dimension.width")
    @Mapping(target = "height", source = "dimension.height")
    @Mapping(target = "depth", source = "dimension.depth")
    @Mapping(target = "weight", source = "weight")
    @Mapping(target = "fragile", source = "fragile")
    @Mapping(target = "quantity", constant = "0L")
    WarehouseProduct toEntity(NewProductInWarehouseRequest dto);

    @Mapping(target = "deliveryWeight", source = "product.weight")
    @Mapping(target = "deliveryVolume", expression = "java(calculateVolume(product))")
    @Mapping(target = "fragile", source = "product.fragile")
    BookedProductsDto toBookedProductsDto(WarehouseProduct product);

    default Double calculateVolume(WarehouseProduct product) {
        if (product == null) {
            return 0.0;
        }
        return product.getWidth() * product.getHeight() * product.getDepth();
    }
}
