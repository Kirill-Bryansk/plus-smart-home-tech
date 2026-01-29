package ru.yandex.practicum.telemetry.analyzer.consumer.hub.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.analyzer.model.Sensor;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;

/**
 * Сервис для работы с датчиками (устройствами)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorRepository sensorRepository;

    /**
     * Гарантирует, что датчик существует в указанном хабе.
     * Если датчика нет - создает его.
     *
     * @param sensorId идентификатор датчика
     * @param hubId идентификатор хаба
     */
    public void ensureSensorExists(String sensorId, String hubId) {
        sensorRepository.findById(sensorId).ifPresentOrElse(
                existing -> {
                    // Проверяем, что датчик принадлежит правильному хабу
                    if (!hubId.equals(existing.getHubId())) {
                        log.warn("⚠️ Датчик {} принадлежит другому хабу: expected={}, actual={}",
                                sensorId, hubId, existing.getHubId());
                    }
                    // Датчик уже существует - ничего не делаем
                },
                () -> {
                    // Создаем новый датчик
                    Sensor sensor = new Sensor();
                    sensor.setId(sensorId);
                    sensor.setHubId(hubId);
                    sensorRepository.save(sensor);
                    log.debug("➕ Создан датчик: id={}, hubId={}", sensorId, hubId);
                }
        );
    }

    /**
     * Проверяет существование датчика в указанном хабе
     */
    public boolean sensorExists(String sensorId, String hubId) {
        return sensorRepository.findByIdAndHubId(sensorId, hubId).isPresent();
    }
}