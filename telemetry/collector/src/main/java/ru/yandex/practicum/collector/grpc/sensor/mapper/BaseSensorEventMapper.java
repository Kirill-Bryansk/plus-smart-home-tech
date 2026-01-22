package ru.yandex.practicum.collector.grpc.sensor.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.collector.dto.sensor.SensorEventDto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

/**
 * Базовый класс для всех мапперов событий сенсоров.
 * Содержит общую логику обработки timestamp и базовых полей.
 */
@Slf4j
public abstract class BaseSensorEventMapper<T extends SensorEventDto> implements SensorEventMapper {

    /**
     * Заполняет базовые поля DTO (id, hubId, timestamp) из Protobuf сообщения
     * @param dto Целевой DTO объект
     * @param proto Исходное Protobuf сообщение
     */
    protected void fillCommonFields(T dto, SensorEventProto proto) {
        log.debug("Заполнение общих полей для события ID={}", proto.getId());

        // Устанавливаем обязательные поля
        dto.setId(proto.getId());
        dto.setHubId(proto.getHubId());
        log.debug("Установлены базовые поля: ID={}, Hub={}", proto.getId(), proto.getHubId());

        // Обрабатываем timestamp: из proto или текущее время
        processTimestamp(dto, proto);
    }

    /**
     * Обрабатывает timestamp: использует из proto, если есть, иначе текущее время
     * @param dto Целевой DTO объект
     * @param proto Исходное Protobuf сообщение
     */
    private void processTimestamp(T dto, SensorEventProto proto) {
        if (proto.hasTimestamp()) {
            com.google.protobuf.Timestamp timestamp = proto.getTimestamp();
            Instant instant = Instant.ofEpochSecond(
                    timestamp.getSeconds(),
                    timestamp.getNanos()
            );
            dto.setTimestamp(instant);
            log.debug("Таймштамп из proto преобразован: {}", instant);
        } else {
            Instant now = Instant.now();
            dto.setTimestamp(now);
            log.debug("Таймштамп не установлен в proto, используется текущее время: {}", now);
        }
    }

    /**
     * Создает и заполняет DTO объект
     * @param -proto Исходное Protobuf сообщение
     * @return Заполненный DTO объект
     */
    protected abstract T createDto();

    /**
     * Заполняет специфичные для типа сенсора поля DTO
     * @param dto Целевой DTO объект
     * @param proto Исходное Protobuf сообщение
     */
    protected abstract void fillSpecificFields(T dto, SensorEventProto proto);

    @Override
    public SensorEventDto mapFromProto(SensorEventProto proto) {
        log.debug("{}: начало маппинга", getClass().getSimpleName());

        // Проверяем наличие данных конкретного сенсора
        validateProto(proto);

        // Создаем DTO
        T dto = createDto();

        // Заполняем общие поля
        fillCommonFields(dto, proto);

        // Заполняем специфичные поля
        fillSpecificFields(dto, proto);

        log.info("{}: создан DTO для события ID={}", getClass().getSimpleName(), proto.getId());
        return dto;
    }

    /**
     * Проверяет наличие данных конкретного сенсора в Protobuf сообщении
     * @param proto Исходное Protobuf сообщение
     * @throws IllegalArgumentException если данные отсутствуют
     */
    protected abstract void validateProto(SensorEventProto proto);
}