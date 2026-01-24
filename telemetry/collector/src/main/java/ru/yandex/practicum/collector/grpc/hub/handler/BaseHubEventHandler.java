package ru.yandex.practicum.collector.grpc.hub.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.collector.grpc.hub.service.HubEventHandler;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.time.Instant;

import static ru.yandex.practicum.collector.config.KafkaConfig.TopicType.HUBS_EVENTS;

/**
 * Базовый класс для обработчиков событий хаба.
 * Реализует общую логику: проверка типа события, преобразование в Avro,
 * отправка в Kafka-топик telemetry.hubs.v1.
 * Наследники должны реализовать mapToAvro() для конкретного типа события.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class BaseHubEventHandler implements HubEventHandler {
    protected final KafkaEventProducer producer;

    /**
     * Преобразует Protobuf-событие в Avro-представление для Kafka.
     * @param event Событие в формате Protobuf (HubEventProto)
     * @return Avro-объект, готовый к отправке в Kafka
     */
    protected abstract SpecificRecordBase mapToAvro(HubEventProto event);

    /**
     * Основной метод обработки Hub-события.
     * 1. Проверяет, что тип события соответствует обработчику
     * 2. Преобразует Protobuf → Avro
     * 3. Отправляет в Kafka-топик для Hub-событий
     * @param event Входящее событие от хаба
     */
    @Override
    public void handle(HubEventProto event) {
        log.debug("Начало обработки Hub-события типа: {}", getMessageType());
        log.info("Отправка Hub-события. Ключ (hubId)={}, Тип={}",
                event.getHubId(), event.getPayloadCase());
        if (!event.getPayloadCase().equals(getMessageType())) {
            String errorMsg = String.format("Несоответствие типа: обработчик ожидает %s, а получил %s",
                    getMessageType(), event.getPayloadCase());
            log.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        SpecificRecordBase payload = mapToAvro(event);

        Instant timestamp = Instant.ofEpochSecond(
                event.getTimestamp().getSeconds(),
                event.getTimestamp().getNanos()
        );

        log.info("Отправка Hub-события {} в Kafka, хаб: {}",
                event.getPayloadCase(), event.getHubId());

        producer.send(payload, event.getHubId(), timestamp, HUBS_EVENTS);

        log.debug("Hub-событие успешно отправлено в Kafka");
    }
}