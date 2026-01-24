package ru.yandex.practicum.collector.grpc.sensor.mapper.device;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.sensor.ClimateSensorEventDto;
import ru.yandex.practicum.collector.enums.SensorEventType;
import ru.yandex.practicum.collector.grpc.sensor.mapper.BaseSensorEventMapper;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Slf4j
@Component
public class ClimateSensorEventMapper extends BaseSensorEventMapper<ClimateSensorEventDto> {

    @Override
    protected ClimateSensorEventDto createDto() {
        return new ClimateSensorEventDto();
    }

    // TODO: Проверка избыточна - тип уже определён в GrpcToDtoMapper через SensorEventTypeMapper.
// Можно удалить эту проверку после подтверждения корректности работы typeMapper.
    @Override
    protected void validateProto(SensorEventProto proto) {
        if (!proto.hasClimateSensorEvent()) {
            log.error("ОШИБКА: В прото отсутствуют данные климатического датчика");
            throw new IllegalArgumentException("Отсутствуют данные климатического датчика");
        }
    }

    @Override
    protected void fillSpecificFields(ClimateSensorEventDto dto, SensorEventProto proto) {
        ClimateSensorProto climateData = proto.getClimateSensorEvent();

        log.debug("Данные климатического датчика: temp={}°C, humidity={}%, CO2={}ppm",
                climateData.getTemperatureC(), climateData.getHumidity(), climateData.getCo2Level());

        dto.setTemperatureC(climateData.getTemperatureC());
        dto.setHumidity(climateData.getHumidity());
        dto.setCo2Level(climateData.getCo2Level());

        log.debug("Установлены специфичные поля климатического датчика");
    }

    @Override
    public SensorEventType getSupportedType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}