package ru.yandex.practicum.collector.grpc.sensor.mapper.device;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.sensor.SwitchSensorEventDto;
import ru.yandex.practicum.collector.enums.SensorEventType;
import ru.yandex.practicum.collector.grpc.sensor.mapper.BaseSensorEventMapper;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Slf4j
@Component
public class SwitchSensorEventMapper extends BaseSensorEventMapper<SwitchSensorEventDto> {

    @Override
    protected SwitchSensorEventDto createDto() {
        return new SwitchSensorEventDto();
    }

    // TODO: Проверка избыточна - тип уже определён в GrpcToDtoMapper через SensorEventTypeMapper.
// Можно удалить эту проверку после подтверждения корректности работы typeMapper.
    @Override
    protected void validateProto(SensorEventProto proto) {
        if (!proto.hasSwitchSensor()) {
            log.error("ОШИБКА: В прото отсутствуют данные датчика-переключателя");
            throw new IllegalArgumentException("Отсутствуют данные датчика-переключателя");
        }
    }

    @Override
    protected void fillSpecificFields(SwitchSensorEventDto dto, SensorEventProto proto) {
        SwitchSensorProto switchData = proto.getSwitchSensor();

        log.debug("Данные датчика-переключателя: state={}", switchData.getState());

        dto.setState(switchData.getState());

        log.debug("Установлено состояние переключателя: {}", switchData.getState());
    }

    @Override
    public SensorEventType getSupportedType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }
}