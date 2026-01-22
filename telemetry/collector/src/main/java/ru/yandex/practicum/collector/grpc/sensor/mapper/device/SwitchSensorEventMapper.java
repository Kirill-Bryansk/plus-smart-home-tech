package ru.yandex.practicum.collector.grpc.sensor.mapper.device;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.sensor.SwitchSensorEventDto;
import ru.yandex.practicum.collector.dto.sensor.SensorEventDto;
import ru.yandex.practicum.collector.enums.SensorEventType;
import ru.yandex.practicum.collector.grpc.sensor.mapper.SensorEventMapper;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Slf4j
@Component
public class SwitchSensorEventMapper implements SensorEventMapper {

    @Override
    public SensorEventDto mapFromProto(SensorEventProto proto) {
        log.debug("SwitchSensorEventMapper: начало маппинга датчика-переключателя");

        if (!proto.hasSwitchSensor()) {
            log.error("ОШИБКА: В прото отсутствуют данные датчика-переключателя");
            throw new IllegalArgumentException("Отсутствуют данные датчика-переключателя");
        }

        SwitchSensorProto switchData = proto.getSwitchSensor();
        log.debug("Данные датчика-переключателя: state={}", switchData.getState());

        SwitchSensorEventDto dto = new SwitchSensorEventDto();
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

        dto.setState(switchData.getState());
        log.debug("Установлено состояние переключателя: {}", switchData.getState());

        log.info("SwitchSensorEventMapper: создан DTO для события ID={}", proto.getId());
        return dto;
    }

    @Override
    public SensorEventType getSupportedType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }
}