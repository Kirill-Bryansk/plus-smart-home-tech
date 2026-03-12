package ru.yandex.practicum.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.yandex.practicum.model.dto.payment.PaymentDto;
import ru.yandex.practicum.payment.model.Payment;

/**
 * Маппер для конвертации между Payment Entity и PaymentDto.
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper {

    /**
     * Конвертировать Payment Entity в PaymentDto.
     */
    PaymentDto toDto(Payment payment);

    /**
     * Конвертировать PaymentDto в Payment Entity.
     */
    Payment toEntity(PaymentDto paymentDto);

    /**
     * Обновить существующий Payment Entity из PaymentDto.
     */
    void updateEntityFromDto(PaymentDto paymentDto, @MappingTarget Payment payment);
}
