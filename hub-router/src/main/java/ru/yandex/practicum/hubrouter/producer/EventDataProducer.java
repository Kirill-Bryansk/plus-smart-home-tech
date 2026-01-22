package ru.yandex.practicum.hubrouter.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.hubrouter.config.SensorConfig;
import ru.yandex.practicum.hubrouter.emulator.*;
import ru.yandex.practicum.hubrouter.service.GrpcClientService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventDataProducer {

    private final SensorConfig sensorConfig;
    private final GrpcClientService grpcClientService;
    private final List<SensorEventFactory> factories = new ArrayList<>();
    private boolean factoriesInitialized = false;

    public synchronized void initializeFactories() {
        if (factoriesInitialized) {
            return;
        }

        log.info("Инициализация фабрик событий...");

        int count = 0;

        if (sensorConfig.getMotionSensors() != null) {
            sensorConfig.getMotionSensors().forEach(config ->
                    factories.add(new MotionSensorEventFactory(config))
            );
            count += sensorConfig.getMotionSensors().size();
            log.debug("Добавлено {} motion датчиков", sensorConfig.getMotionSensors().size());
        }

        if (sensorConfig.getSwitchSensors() != null) {
            sensorConfig.getSwitchSensors().forEach(config ->
                    factories.add(new SwitchSensorEventFactory(config))
            );
            count += sensorConfig.getSwitchSensors().size();
            log.debug("Добавлено {} switch датчиков", sensorConfig.getSwitchSensors().size());
        }

        if (sensorConfig.getTemperatureSensors() != null) {
            sensorConfig.getTemperatureSensors().forEach(config ->
                    factories.add(new TemperatureSensorEventFactory(config))
            );
            count += sensorConfig.getTemperatureSensors().size();
            log.debug("Добавлено {} temperature датчиков", sensorConfig.getTemperatureSensors().size());
        }

        if (sensorConfig.getLightSensors() != null) {
            sensorConfig.getLightSensors().forEach(config ->
                    factories.add(new LightSensorEventFactory(config))
            );
            count += sensorConfig.getLightSensors().size();
            log.debug("Добавлено {} light датчиков", sensorConfig.getLightSensors().size());
        }

        if (sensorConfig.getClimateSensors() != null) {
            sensorConfig.getClimateSensors().forEach(config ->
                    factories.add(new ClimateSensorEventFactory(config))
            );
            count += sensorConfig.getClimateSensors().size();
            log.debug("Добавлено {} climate датчиков", sensorConfig.getClimateSensors().size());
        }

        factoriesInitialized = true;
        log.info("Создано {} фабрик событий (всего {} датчиков)", factories.size(), count);
    }

    @Scheduled(fixedDelay = 5000) // Отправляем события каждые 5 секунд
    public void generateAndSendEvents() {
        if (!factoriesInitialized) {
            initializeFactories();
        }

        if (factories.isEmpty()) {
            log.warn("Нет фабрик для генерации событий");
            return;
        }

        log.debug("Генерация событий для {} датчиков...", factories.size());

        int successCount = 0;
        int errorCount = 0;

        for (SensorEventFactory factory : factories) {
            try {
                SensorEventProto event = factory.createEvent();
                grpcClientService.sendEvent(event);
                successCount++;
                log.trace("Отправлено событие: {}", event.getId());
            } catch (Exception e) {
                errorCount++;
                log.error("Ошибка при создании/отправке события: {}", e.getMessage());
            }
        }

        log.debug("Отправлено успешно: {}, с ошибками: {}", successCount, errorCount);
    }
}