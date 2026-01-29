package ru.yandex.practicum.telemetry.analyzer.consumer.hub.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;

/**
 * Обработчик события удаления устройства из хаба
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceRemovedHandler implements EventHandler {

    private final SensorRepository sensorRepository;

    @Override
    public boolean canHandle(Object payload) {
        return payload instanceof DeviceRemovedEventAvro;
    }

    @Override
    public void handle(String hubId, Object payload) {
        DeviceRemovedEventAvro event = (DeviceRemovedEventAvro) payload;
        String deviceId = event.getId();

        log.debug("➖ Обрабатываю удаление устройства: hubId={}, deviceId={}", hubId, deviceId);

        // Ищем устройство в указанном хабе
        sensorRepository.findByIdAndHubId(deviceId, hubId).ifPresentOrElse(
                sensor -> {
                    // Удаляем устройство
                    sensorRepository.delete(sensor);
                    log.info("✅ Устройство удалено: id={}, hubId={}", deviceId, hubId);
                },
                () -> log.warn("⚠️ Устройство не найдено для удаления: id={}, hubId={}", deviceId, hubId)
        );
    }
}