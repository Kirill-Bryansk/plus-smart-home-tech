package ru.yandex.practicum.collector.grpc.hub.handler.device;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.grpc.hub.handler.BaseHubEventHandler;
import ru.yandex.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;

/**
 * Обработчик события добавления устройства (DEVICE_ADDED).
 * Преобразует Protobuf-сообщение в Avro-формат и отправляет в Kafka-топик для Hub-событий.
 * Поддерживаемый тип: HubEventProto.PayloadCase.DEVICE_ADDED
 */
@Slf4j
@Component("grpcDeviceAddedEventHandler")
public class DeviceAddedEventHandler extends BaseHubEventHandler {

    /**
     * Конструктор с внедрением зависимости Kafka продюсера.
     * @param producer компонент для отправки сообщений в Kafka
     */
    public DeviceAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
        log.debug("Инициализирован DeviceAddedEventHandler");
    }

    /**
     * Возвращает тип события, который обрабатывает этот хендлер.
     * @return HubEventProto.PayloadCase.DEVICE_ADDED
     */
    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    /**
     * Преобразует Protobuf-событие добавления устройства в Avro-представление.
     * Маппит все поля: id устройства и его тип.
     *
     * @param event HubEventProto с заполненным полем device_added
     * @return DeviceAddedEventAvro готовый для отправки в Kafka
     * @throws IllegalArgumentException если поле device_added не установлено
     */
    @Override
    protected SpecificRecordBase mapToAvro(HubEventProto event) {
        log.debug("Преобразование HubEventProto → HubEventAvro для DEVICE_ADDED");

        DeviceAddedEventProto deviceEvent = event.getDeviceAdded();

        DeviceTypeAvro deviceTypeAvro = DeviceTypeAvro.valueOf(deviceEvent.getType().name());

        DeviceAddedEventAvro deviceAvro = DeviceAddedEventAvro.newBuilder()
                .setId(deviceEvent.getId())
                .setType(deviceTypeAvro)
                .build();

        Instant timestamp = Instant.ofEpochSecond(
                event.getTimestamp().getSeconds(),
                event.getTimestamp().getNanos()
        );

        HubEventAvro hubEvent = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(timestamp)
                .setPayload(deviceAvro)
                .build();

        log.debug("HubEventAvro создан: hubId={}, тип payload=DeviceAddedEventAvro",
                event.getHubId());
        return hubEvent;
    }
}