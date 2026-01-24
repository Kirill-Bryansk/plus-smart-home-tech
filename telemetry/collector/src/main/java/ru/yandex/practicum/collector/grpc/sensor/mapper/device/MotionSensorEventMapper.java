package ru.yandex.practicum.collector.grpc.sensor.mapper.device;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.sensor.MotionSensorEventDto;
import ru.yandex.practicum.collector.enums.SensorEventType;
import ru.yandex.practicum.collector.grpc.sensor.mapper.BaseSensorEventMapper;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Slf4j
@Component
public class MotionSensorEventMapper extends BaseSensorEventMapper<MotionSensorEventDto> {

    @Override
    protected MotionSensorEventDto createDto() {
        return new MotionSensorEventDto();
    }

    // TODO: Проверка избыточна - тип уже определён в GrpcToDtoMapper через SensorEventTypeMapper.
// Можно удалить эту проверку после подтверждения корректности работы typeMapper.
    @Override
    protected void validateProto(SensorEventProto proto) {
        if (!proto.hasMotionSensorEvent()) {
            log.error("ОШИБКА: В прото отсутствуют данные датчика движения");
            throw new IllegalArgumentException("Отсутствуют данные датчика движения");
        }
    }

    @Override
    protected void fillSpecificFields(MotionSensorEventDto dto, SensorEventProto proto) {
        MotionSensorProto motionData = proto.getMotionSensorEvent();

        log.debug("Данные датчика движения: linkQuality={}, motion={}, voltage={}V",
                motionData.getLinkQuality(), motionData.getMotion(), motionData.getVoltage());

        dto.setLinkQuality(motionData.getLinkQuality());
        dto.setMotion(motionData.getMotion());
        dto.setVoltage(motionData.getVoltage());

        log.debug("Установлены специфичные поля датчика движения");
    }

    @Override
    public SensorEventType getSupportedType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}