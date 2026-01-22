package ru.yandex.practicum.collector.grpc.sensor.mapper.device;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.sensor.LightSensorEventDto;
import ru.yandex.practicum.collector.enums.SensorEventType;
import ru.yandex.practicum.collector.grpc.sensor.mapper.BaseSensorEventMapper;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Slf4j
@Component
public class LightSensorEventMapper extends BaseSensorEventMapper<LightSensorEventDto> {

    @Override
    protected LightSensorEventDto createDto() {
        return new LightSensorEventDto();
    }

    // TODO: Проверка избыточна - тип уже определён в GrpcToDtoMapper через SensorEventTypeMapper.
// Можно удалить эту проверку после подтверждения корректности работы typeMapper.
    @Override
    protected void validateProto(SensorEventProto proto) {
        if (!proto.hasLightSensor()) {
            log.error("ОШИБКА: В прото отсутствуют данные светового датчика");
            throw new IllegalArgumentException("Отсутствуют данные светового датчика");
        }
    }

    @Override
    protected void fillSpecificFields(LightSensorEventDto dto, SensorEventProto proto) {
        LightSensorProto lightData = proto.getLightSensor();

        log.debug("Данные светового датчика: linkQuality={}, luminosity={}",
                lightData.getLinkQuality(), lightData.getLuminosity());

        dto.setLinkQuality(lightData.getLinkQuality());
        dto.setLuminosity(lightData.getLuminosity());

        log.debug("Установлены специфичные поля светового датчика");
    }

    @Override
    public SensorEventType getSupportedType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}