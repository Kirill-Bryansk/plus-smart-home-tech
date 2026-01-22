package ru.yandex.practicum.collector.grpc.sensor.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.sensor.SensorEventDto;
import ru.yandex.practicum.collector.enums.SensorEventType;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GrpcToDtoMapper {

    private final SensorEventTypeMapper typeMapper;
    private final Map<SensorEventType, SensorEventMapper> mappers;

    /**
     * Конструктор получает все мапперы через Spring
     */
    public GrpcToDtoMapper(SensorEventTypeMapper typeMapper, List<SensorEventMapper> allMappers) {
        this.typeMapper = typeMapper;
        this.mappers = new HashMap<>();

        // Регистрируем каждый маппер по его типу
        for (SensorEventMapper mapper : allMappers) {
            mappers.put(mapper.getSupportedType(), mapper);
        }
    }

    /**
     * Основной метод преобразования Protobuf → DTO
     */
    public SensorEventDto fromProto(SensorEventProto proto) {
        // 1. Проверка входных данных
        if (proto == null) {
            throw new IllegalArgumentException("SensorEventProto cannot be null");
        }

        // 2. Определяем тип с помощью SensorEventTypeMapper
        SensorEventType eventType = typeMapper.fromProto(proto);

        // 3. Находим соответствующий маппер
        SensorEventMapper mapper = mappers.get(eventType);
        if (mapper == null) {
            throw new IllegalArgumentException(
                    "No mapper found for sensor type: " + eventType
            );
        }

        // 4. Делегируем преобразование конкретному мапперу
        return mapper.mapFromProto(proto);
    }
}
