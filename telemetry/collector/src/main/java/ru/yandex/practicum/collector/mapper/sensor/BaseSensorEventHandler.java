package ru.yandex.practicum.collector.mapper.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.collector.dto.sensor.SensorEventDto;
import ru.yandex.practicum.collector.enums.SensorEventType;
import ru.yandex.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import static ru.yandex.practicum.collector.config.KafkaConfig.TopicType.SENSORS_EVENTS;

public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {

    protected final KafkaEventProducer producer;

    protected BaseSensorEventHandler(KafkaEventProducer producer) {
        this.producer = producer;
    }

    protected abstract SensorEventType getSupportedType();
    protected abstract T mapToAvro(SensorEventDto event);

    @Override
    public boolean canHandle(SensorEventDto event) {
        return event.getType().equals(getSupportedType());
    }

    @Override
    public void handle(SensorEventDto event) {
        T payload = mapToAvro(event);

        SensorEventAvro eventAvro = SensorEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setId(event.getId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();

        producer.send(eventAvro, event.getHubId(), event.getTimestamp(), SENSORS_EVENTS);
    }
}