package ru.yandex.practicum.collector.mapper.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.sensor.SensorEventDto;
import ru.yandex.practicum.collector.dto.sensor.SwitchSensorEventDto;
import ru.yandex.practicum.collector.enums.SensorEventType;
import ru.yandex.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Slf4j
@Component
public class SwitchSensorEventHandler extends BaseSensorEventHandler<SwitchSensorAvro> {

    public SwitchSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
        log.info("=== SwitchSensorEventHandler СОЗДАН ===");
        log.info("Producer: {}", producer != null ? "ЕСТЬ" : "NULL!");
    }

    @Override
    protected SensorEventType getSupportedType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }

    @Override
    protected SwitchSensorAvro mapToAvro(SensorEventDto event) {
        log.info("=== mapToAvro вызывается! ===");
        SwitchSensorEventDto dto = (SwitchSensorEventDto) event;
        log.info("Конвертация DTO в Avro, state={}", dto.isState());
        return SwitchSensorAvro.newBuilder()
                .setState(dto.isState())
                .build();
    }
}