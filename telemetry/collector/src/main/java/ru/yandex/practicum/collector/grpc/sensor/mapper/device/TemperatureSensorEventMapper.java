package ru.yandex.practicum.collector.grpc.sensor.mapper.device;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.sensor.TemperatureSensorEventDto;
import ru.yandex.practicum.collector.enums.SensorEventType;
import ru.yandex.practicum.collector.grpc.sensor.mapper.BaseSensorEventMapper;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Slf4j
@Component
public class TemperatureSensorEventMapper extends BaseSensorEventMapper<TemperatureSensorEventDto> {

    @Override
    protected TemperatureSensorEventDto createDto() {
        return new TemperatureSensorEventDto();
    }

    // TODO: Проверка избыточна - тип уже определён в GrpcToDtoMapper через SensorEventTypeMapper.
    // Можно удалить эту проверку после подтверждения корректности работы typeMapper.
    @Override
    protected void validateProto(SensorEventProto proto) {
        if (!proto.hasTemperatureSensor()) {
            log.error("ОШИБКА: В прото отсутствуют данные температурного датчика");
            throw new IllegalArgumentException("Отсутствуют данные температурного датчика");
        }
    }

    @Override
    protected void fillSpecificFields(TemperatureSensorEventDto dto, SensorEventProto proto) {
        TemperatureSensorProto tempData = proto.getTemperatureSensor();

        log.debug("Данные температурного датчика: tempC={}°C, tempF={}°F",
                tempData.getTemperatureC(), tempData.getTemperatureF());

        dto.setTemperatureC(tempData.getTemperatureC());
        dto.setTemperatureF(tempData.getTemperatureF());

        log.debug("Установлены специфичные поля температурного датчика");
    }

    @Override
    public SensorEventType getSupportedType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}