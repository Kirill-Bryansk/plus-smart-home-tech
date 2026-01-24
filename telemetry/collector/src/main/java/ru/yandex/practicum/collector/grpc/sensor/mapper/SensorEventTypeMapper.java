package ru.yandex.practicum.collector.grpc.sensor.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.enums.SensorEventType;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Slf4j
@Component
public class SensorEventTypeMapper {

    public SensorEventType fromProto(SensorEventProto proto) {
        log.debug("SensorEventTypeMapper: определение типа события");

        if (proto == null) {
            log.error("КРИТИЧЕСКАЯ ОШИБКА: Входной proto равен null");
            throw new IllegalArgumentException("Входное сообщение не может быть null");
        }

        SensorEventProto.PayloadCase payloadCase = proto.getPayloadCase();
        log.debug("Определен PayloadCase: {}", payloadCase);

        SensorEventType result;

        switch (payloadCase) {
            case MOTION_SENSOR_EVENT:
                result = SensorEventType.MOTION_SENSOR_EVENT;
                break;
            case TEMPERATURE_SENSOR_EVENT:
                result = SensorEventType.TEMPERATURE_SENSOR_EVENT;
                break;
            case LIGHT_SENSOR_EVENT:
                result = SensorEventType.LIGHT_SENSOR_EVENT;
                break;
            case CLIMATE_SENSOR_EVENT:
                result = SensorEventType.CLIMATE_SENSOR_EVENT;
                break;
            case SWITCH_SENSOR_EVENT:
                result = SensorEventType.SWITCH_SENSOR_EVENT;
                break;
            case PAYLOAD_NOT_SET:
                log.error("ОШИБКА: Полезная нагрузка не установлена в прото");
                throw new IllegalArgumentException("Полезная нагрузка не установлена в прото");
            default:
                log.error("НЕИЗВЕСТНЫЙ ТИП: Неподдерживаемый PayloadCase: {}", payloadCase);
                throw new IllegalArgumentException("Неизвестный тип события сенсора: " + payloadCase);
        }

        log.debug("Определен тип события: {}", result);
        return result;
    }
}