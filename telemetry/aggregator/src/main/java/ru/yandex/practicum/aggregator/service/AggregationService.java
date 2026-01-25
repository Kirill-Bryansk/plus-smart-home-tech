package ru.yandex.practicum.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Сервис агрегации событий датчиков в снапшоты.
 * Хранит текущее состояние всех датчиков по хабам.
 */
@Slf4j
@Service
public class AggregationService {

    /**
     * Хранилище снапшотов по hubId.
     * Ключ: hubId, Значение: снапшот состояния всех датчиков хаба
     */
    private final Map<String, SensorsSnapshotAvro> snapshotsByHubId = new HashMap<>();

    /**
     * Агрегирует событие датчика в снапшот.
     * Обновляет состояние только если данные новее или изменились.
     *
     * @param event событие от датчика
     * @return Optional с обновленным снапшотом, если состояние изменилось
     */
    public Optional<SensorsSnapshotAvro> aggregateEvent(SensorEventAvro event) {
        String hubId = event.getHubId();
        String sensorId = event.getId();

        log.debug("Обработка события от датчика {} хаба {}", sensorId, hubId);

        // Получаем или создаем снапшот для хаба
        SensorsSnapshotAvro hubSnapshot = snapshotsByHubId.computeIfAbsent(hubId, hubIdKey -> {
            log.debug("Создание нового снапшота для хаба {}", hubIdKey);
            SensorsSnapshotAvro newSnapshot = new SensorsSnapshotAvro();
            newSnapshot.setHubId(hubIdKey);
            newSnapshot.setTimestamp(event.getTimestamp()); // Instant
            newSnapshot.setSensorsState(new HashMap<>());
            return newSnapshot;
        });

        Map<String, SensorStateAvro> sensorsState = hubSnapshot.getSensorsState();
        SensorStateAvro oldState = sensorsState.get(sensorId);

        // Проверяем, нужно ли обновлять данные датчика
        if (oldState != null) {
            boolean isOlderTimestamp = event.getTimestamp().isBefore(oldState.getTimestamp());
            boolean isSameData = event.getPayload().equals(oldState.getData());

            if (isOlderTimestamp) {
                log.debug("Событие старше текущего состояния: датчик {}", sensorId);
                return Optional.empty();
            }

            if (isSameData) {
                log.debug("Данные не изменились: датчик {}", sensorId);
                return Optional.empty();
            }
        }

        // Создаем новое состояние датчика
        SensorStateAvro updatedSensorState = new SensorStateAvro();
        updatedSensorState.setTimestamp(event.getTimestamp()); // Instant
        updatedSensorState.setData(event.getPayload());

        // Обновляем состояние
        sensorsState.put(sensorId, updatedSensorState);
        hubSnapshot.setTimestamp(event.getTimestamp()); // Instant

        log.debug("Обновлено состояние датчика {} в хабе {}", sensorId, hubId);
        return Optional.of(hubSnapshot);
    }
}