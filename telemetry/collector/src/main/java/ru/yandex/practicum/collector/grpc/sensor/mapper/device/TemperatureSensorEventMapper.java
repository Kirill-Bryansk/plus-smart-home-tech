package ru.yandex.practicum.collector.grpc.sensor.mapper.device;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.sensor.TemperatureSensorEventDto;
import ru.yandex.practicum.collector.dto.sensor.SensorEventDto;
import ru.yandex.practicum.collector.enums.SensorEventType;
import ru.yandex.practicum.collector.grpc.sensor.mapper.SensorEventMapper;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Slf4j
@Component
public class TemperatureSensorEventMapper implements SensorEventMapper {

    @Override
    public SensorEventDto mapFromProto(SensorEventProto proto) {
        log.debug("TemperatureSensorEventMapper: начало маппинга температурного датчика");

        if (!proto.hasTemperatureSensor()) {
            log.error("ОШИБКА: В прото отсутствуют данные температурного датчика");
            throw new IllegalArgumentException("Отсутствуют данные температурного датчика");
        }

        TemperatureSensorProto tempData = proto.getTemperatureSensor();
        log.debug("Данные температурного датчика: tempC={}°C, tempF={}°F",
                tempData.getTemperatureC(), tempData.getTemperatureF());

        TemperatureSensorEventDto dto = new TemperatureSensorEventDto();
        dto.setId(proto.getId());
        dto.setHubId(proto.getHubId());
        log.debug("Установлены базовые поля: ID={}, Hub={}", proto.getId(), proto.getHubId());

        // Используем timestamp из proto, если он есть, иначе - текущее время (как в REST)
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
            log.debug("Таймштамп не установлен в прото, используется текущее время: {}", now);
        }

        dto.setTemperatureC(tempData.getTemperatureC());
        dto.setTemperatureF(tempData.getTemperatureF());
        log.debug("Установлены специфичные поля температурного датчика");

        log.info("TemperatureSensorEventMapper: создан DTO для события ID={}", proto.getId());
        return dto;
    }

    @Override
    public SensorEventType getSupportedType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}