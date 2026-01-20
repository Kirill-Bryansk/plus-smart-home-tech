package ru.yandex.practicum.collector.mapper.sensor;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.collector.dto.sensor.SensorEventDto;
import ru.yandex.practicum.collector.enums.SensorEventType;
import ru.yandex.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import static ru.yandex.practicum.collector.config.KafkaConfig.TopicType.SENSORS_EVENTS;

@Slf4j
public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {

    protected final KafkaEventProducer producer;

    protected BaseSensorEventHandler(KafkaEventProducer producer) {
        log.info("=== BaseSensorEventHandler конструктор ===");
        log.info("Класс: {}", this.getClass().getSimpleName());
        log.info("Producer инжектирован: {}", producer != null);

        this.producer = producer;
    }

    @Override
    public boolean canHandle(SensorEventDto event) {
        boolean canHandle = event.getType().equals(getSupportedType());
        log.info("canHandle для {}: {}", event.getType(), canHandle);
        return canHandle;
    }

    @Override
    public void handle(SensorEventDto event) {
        log.info("=== НАЧАЛО handle() в {} ===", this.getClass().getSimpleName());
        log.info("Событие: {}", event);

        T payload = mapToAvro(event);
        log.info("Avro payload создан: {}", payload);

        SensorEventAvro eventAvro = SensorEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setId(event.getId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();

        log.info("Вызываю producer.send()...");
        producer.send(eventAvro, event.getHubId(), event.getTimestamp(), SENSORS_EVENTS);

        log.info("=== КОНЕЦ handle() ===");
    }

    protected abstract SensorEventType getSupportedType();

    protected abstract T mapToAvro(SensorEventDto event);
}