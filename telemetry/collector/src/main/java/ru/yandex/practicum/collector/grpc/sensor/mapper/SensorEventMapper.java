package ru.yandex.practicum.collector.grpc.sensor.mapper;

import ru.yandex.practicum.collector.dto.sensor.SensorEventDto;
import ru.yandex.practicum.collector.enums.SensorEventType;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventMapper {
    /**
     * Преобразует Protobuf сообщение в DTO
     */
    SensorEventDto mapFromProto(SensorEventProto proto);

    /**
     * Возвращает тип сенсора, который этот маппер обрабатывает
     */
    SensorEventType getSupportedType();
}
