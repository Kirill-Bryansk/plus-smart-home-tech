package ru.yandex.practicum.collector.grpc.sensor.mapper.device;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.sensor.MotionSensorEventDto;
import ru.yandex.practicum.collector.dto.sensor.SensorEventDto;
import ru.yandex.practicum.collector.enums.SensorEventType;
import ru.yandex.practicum.collector.grpc.sensor.mapper.SensorEventMapper;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Slf4j
@Component
public class MotionSensorEventMapper implements SensorEventMapper {

    @Override
    public SensorEventDto mapFromProto(SensorEventProto proto) {
        log.debug("MotionSensorEventMapper: начало маппинга датчика движения");

        if (!proto.hasMotionSensor()) {
            log.error("ОШИБКА: В прото отсутствуют данные датчика движения");
            throw new IllegalArgumentException("Отсутствуют данные датчика движения");
        }

        MotionSensorProto motionData = proto.getMotionSensor();
        log.debug("Данные датчика движения: linkQuality={}, motion={}, voltage={}V",
                motionData.getLinkQuality(), motionData.getMotion(), motionData.getVoltage());

        MotionSensorEventDto dto = new MotionSensorEventDto();
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

        dto.setLinkQuality(motionData.getLinkQuality());
        dto.setMotion(motionData.getMotion());
        dto.setVoltage(motionData.getVoltage());
        log.debug("Установлены специфичные поля датчика движения");

        log.info("MotionSensorEventMapper: создан DTO для события ID={}", proto.getId());
        return dto;
    }

    @Override
    public SensorEventType getSupportedType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}