package ru.yandex.practicum.collector.grpc.sensor.mapper.device;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.sensor.LightSensorEventDto;
import ru.yandex.practicum.collector.dto.sensor.SensorEventDto;
import ru.yandex.practicum.collector.enums.SensorEventType;
import ru.yandex.practicum.collector.grpc.sensor.mapper.SensorEventMapper;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Slf4j
@Component
public class LightSensorEventMapper implements SensorEventMapper {

    @Override
    public SensorEventDto mapFromProto(SensorEventProto proto) {
        log.debug("LightSensorEventMapper: начало маппинга");

        // Проверяем наличие данных
        if (!proto.hasLightSensor()) {
            log.error("ОШИБКА: В прото отсутствуют данные светового датчика");
            throw new IllegalArgumentException("Отсутствуют данные светового датчика");
        }

        // Получаем данные конкретного сенсора
        LightSensorProto lightData = proto.getLightSensor();
        log.debug("Данные светового датчика: linkQuality={}, luminosity={}",
                lightData.getLinkQuality(), lightData.getLuminosity());

        // Создаем DTO
        LightSensorEventDto dto = new LightSensorEventDto();

        // Заполняем общие поля из proto
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

        // Заполняем специфичные поля
        dto.setLinkQuality(lightData.getLinkQuality());
        dto.setLuminosity(lightData.getLuminosity());
        log.debug("Установлены специфичные поля светового датчика");

        log.info("LightSensorEventMapper: создан DTO для события ID={}", proto.getId());
        return dto;
    }

    @Override
    public SensorEventType getSupportedType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}