package ru.yandex.practicum.collector.grpc.sensor.mapper.device;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.sensor.ClimateSensorEventDto;
import ru.yandex.practicum.collector.dto.sensor.SensorEventDto;
import ru.yandex.practicum.collector.enums.SensorEventType;
import ru.yandex.practicum.collector.grpc.sensor.mapper.SensorEventMapper;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Slf4j
@Component
public class ClimateSensorEventMapper implements SensorEventMapper {

    @Override
    public SensorEventDto mapFromProto(SensorEventProto proto) {
        log.debug("ClimateSensorEventMapper: начало маппинга климатического датчика");

        if (!proto.hasClimateSensor()) {
            log.error("ОШИБКА: В прото отсутствуют данные климатического датчика");
            throw new IllegalArgumentException("Отсутствуют данные климатического датчика");
        }

        ClimateSensorProto climateData = proto.getClimateSensor();
        log.debug("Данные климатического датчика: temp={}°C, humidity={}%, CO2={}ppm",
                climateData.getTemperatureC(), climateData.getHumidity(), climateData.getCo2Level());

        ClimateSensorEventDto dto = new ClimateSensorEventDto();
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

        dto.setTemperatureC(climateData.getTemperatureC());
        dto.setHumidity(climateData.getHumidity());
        dto.setCo2Level(climateData.getCo2Level());
        log.debug("Установлены специфичные поля климатического датчика");

        log.info("ClimateSensorEventMapper: создан DTO для события ID={}", proto.getId());
        return dto;
    }

    @Override
    public SensorEventType getSupportedType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}