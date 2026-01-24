package ru.yandex.practicum.collector.grpc.sensor.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.sensor.SensorEventDto;
import ru.yandex.practicum.collector.enums.SensorEventType;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GrpcToDtoMapper {

    private final SensorEventTypeMapper typeMapper;
    private final Map<SensorEventType, SensorEventMapper> mappers;

    /**
     * Конструктор получает все мапперы через Spring
     */
    public GrpcToDtoMapper(SensorEventTypeMapper typeMapper, List<SensorEventMapper> allMappers) {
        log.info("Инициализация GrpcToDtoMapper...");

        this.typeMapper = typeMapper;
        this.mappers = new HashMap<>();

        // Регистрируем каждый маппер по его типу
        for (SensorEventMapper mapper : allMappers) {
            SensorEventType type = mapper.getSupportedType();
            mappers.put(type, mapper);
            log.debug("Зарегистрирован маппер: {} -> {}", type, mapper.getClass().getSimpleName());
        }

        log.info("GrpcToDtoMapper инициализирован. Зарегистрировано мапперов: {}", mappers.size());
    }

    /**
     * Основной метод преобразования Protobuf → DTO
     */
    public SensorEventDto fromProto(SensorEventProto proto) {
        log.debug("GrpcToDtoMapper: начало преобразования Protobuf → DTO");

        // 1. Проверка входных данных
        if (proto == null) {
            log.error("ОШИБКА: Входное Protobuf сообщение равно null");
            throw new IllegalArgumentException("SensorEventProto cannot be null");
        }

        log.debug("Входные данные: ID={}, Hub={}, PayloadCase={}",
                proto.getId(), proto.getHubId(), proto.getPayloadCase());

        // 2. Определяем тип с помощью SensorEventTypeMapper
        SensorEventType eventType = typeMapper.fromProto(proto);
        log.debug("Определен тип события: {}", eventType);

        // 3. Находим соответствующий маппер
        SensorEventMapper mapper = mappers.get(eventType);
        if (mapper == null) {
            log.error("КРИТИЧЕСКАЯ ОШИБКА: Не найден маппер для типа: {}. Доступные типы: {}",
                    eventType, mappers.keySet());
            throw new IllegalArgumentException(
                    "No mapper found for sensor type: " + eventType
            );
        }

        log.debug("Найден маппер: {}", mapper.getClass().getSimpleName());

        // 4. Делегируем преобразование конкретному мапперу
        SensorEventDto result = mapper.mapFromProto(proto);

        log.debug("GrpcToDtoMapper: преобразование успешно завершено. Тип DTO: {}",
                result.getClass().getSimpleName());

        return result;
    }
}