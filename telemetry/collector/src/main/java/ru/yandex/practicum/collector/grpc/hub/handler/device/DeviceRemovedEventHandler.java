package ru.yandex.practicum.collector.grpc.hub.handler.device;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.grpc.hub.handler.BaseHubEventHandler;
import ru.yandex.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;

/**
 * Обработчик события удаления устройства (DEVICE_REMOVED).
 * Преобразует Protobuf-сообщение в Avro-формат и отправляет в Kafka.
 */
@Slf4j
@Component("grpcDeviceRemovedEventHandler")
public class DeviceRemovedEventHandler extends BaseHubEventHandler {

    public DeviceRemovedEventHandler(KafkaEventProducer producer) {
        super(producer);
        log.debug("Инициализирован DeviceRemovedEventHandler");
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    protected SpecificRecordBase mapToAvro(HubEventProto event) {
        log.debug("Преобразование HubEventProto → HubEventAvro для DEVICE_REMOVED");

        DeviceRemovedEventProto deviceEvent = event.getDeviceRemoved();

        DeviceRemovedEventAvro deviceAvro = DeviceRemovedEventAvro.newBuilder()
                .setId(deviceEvent.getId())
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

        log.debug("HubEventAvro создан: hubId={}, тип payload=DeviceRemovedEventAvro",
                event.getHubId());
        return hubEvent;
    }
}