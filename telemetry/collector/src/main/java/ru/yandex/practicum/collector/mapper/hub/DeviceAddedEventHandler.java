package ru.yandex.practicum.collector.mapper.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.hub.HubEventDto;
import ru.yandex.practicum.collector.dto.hub.device.DeviceAddedEventDto;
import ru.yandex.practicum.collector.enums.HubEventType;
import ru.yandex.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Component
public class DeviceAddedEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {

    public DeviceAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected HubEventType getSupportedType() {
        return HubEventType.DEVICE_ADDED;
    }

    @Override
    protected DeviceAddedEventAvro mapToAvro(HubEventDto event) {
        DeviceAddedEventDto dto = (DeviceAddedEventDto) event;

        // Используем valueOf() вместо switch
        DeviceTypeAvro deviceTypeAvro = DeviceTypeAvro.valueOf(dto.getDeviceType().name());

        return DeviceAddedEventAvro.newBuilder()
                .setId(dto.getId())
                .setType(deviceTypeAvro)
                .build();
    }
}