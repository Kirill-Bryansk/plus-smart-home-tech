package ru.yandex.practicum.collector.mapper.hub;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.collector.dto.hub.HubEventDto;
import ru.yandex.practicum.collector.enums.HubEventType;
import ru.yandex.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import static ru.yandex.practicum.collector.config.KafkaConfig.TopicType.HUBS_EVENTS;

public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    protected final KafkaEventProducer producer;

    protected BaseHubEventHandler(KafkaEventProducer producer) {
        this.producer = producer;
    }

    protected abstract HubEventType getSupportedType();

    protected abstract T mapToAvro(HubEventDto event);

    @Override
    public boolean canHandle(HubEventDto event) {
        return event.getType().equals(getSupportedType());
    }

    @Override
    public void handle(HubEventDto event) {
        T payload = mapToAvro(event);

        HubEventAvro eventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();

        producer.send(eventAvro, event.getHubId(), event.getTimestamp(), HUBS_EVENTS);
    }
}