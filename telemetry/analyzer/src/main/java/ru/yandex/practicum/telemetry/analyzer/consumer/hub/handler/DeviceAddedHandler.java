package ru.yandex.practicum.telemetry.analyzer.consumer.hub.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.telemetry.analyzer.model.Sensor;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;

/**
 * Обработчик события добавления устройства в хаб
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceAddedHandler implements EventHandler {

    private final SensorRepository sensorRepository;

    @Override
    public boolean canHandle(Object payload) {
        return payload instanceof DeviceAddedEventAvro;
    }

    @Override
    public void handle(String hubId, Object payload) {
        DeviceAddedEventAvro event = (DeviceAddedEventAvro) payload;
        String deviceId = event.getId();

        log.debug("➕ Обрабатываю добавление устройства: hubId={}, deviceId={}", hubId, deviceId);

        // Проверяем, существует ли уже устройство
        sensorRepository.findById(deviceId).ifPresentOrElse(
                existing -> {
                    // Устройство уже существует
                    if (!hubId.equals(existing.getHubId())) {
                        log.warn("⚠️ Устройство {} уже принадлежит другому хабу: {} -> {}",
                                deviceId, existing.getHubId(), hubId);
                    } else {
                        log.debug("ℹ️ Устройство {} уже существует в хабе {}", deviceId, hubId);
                    }
                },
                () -> {
                    // Создаем новое устройство
                    Sensor sensor = new Sensor();
                    sensor.setId(deviceId);
                    sensor.setHubId(hubId);
                    sensorRepository.save(sensor);
                    log.info("✅ Устройство добавлено: id={}, hubId={}", deviceId, hubId);
                }
        );
    }
}