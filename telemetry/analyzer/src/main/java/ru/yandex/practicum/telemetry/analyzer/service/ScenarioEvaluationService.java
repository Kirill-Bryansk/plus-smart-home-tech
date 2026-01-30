package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.model.*;
import ru.yandex.practicum.telemetry.analyzer.model.enums.*;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.service.grpc.HubRouterClient;
import ru.yandex.practicum.telemetry.analyzer.mapper.DeviceActionRequestMapper;

import java.util.List;
import java.util.Map;

/**
 * Сервис для анализа сценариев и выполнения действий
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioEvaluationService {

    private final ScenarioRepository scenarioRepository;
    private final HubRouterClient hubRouterClient;
    private final DeviceActionRequestMapper deviceActionRequestMapper;
    /**
     * Анализирует снапшот и выполняет сценарии если условия выполнены
     */
    @Transactional(readOnly = true)
    public void evaluateAndExecute(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        Map<String, SensorStateAvro> sensorStates = snapshot.getSensorsState();

        log.debug("🔍 Анализирую снапшот: hubId={}, датчиков={}", hubId, sensorStates.size());

        // Получаем все сценарии для данного хаба
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);
        if (scenarios.isEmpty()) {
            log.debug("ℹ️ Нет сценариев для хаба {}", hubId);
            return;
        }

        log.debug("📋 Найдено {} сценариев для хаба {}", scenarios.size(), hubId);

        // Проверяем каждый сценарий
        for (Scenario scenario : scenarios) {
            evaluateScenario(scenario, sensorStates);
        }
    }

    /**
     * Проверяет один сценарий и выполняет действия если условия выполнены
     */
    private void evaluateScenario(Scenario scenario, Map<String, SensorStateAvro> sensorStates) {
        String scenarioName = scenario.getName();
        boolean allConditionsMet = true;

        log.debug("🧪 Проверяю сценарий '{}'", scenarioName);

        // Проверяем все условия сценария
        for (Map.Entry<String, Condition> entry : scenario.getConditions().entrySet()) {
            String sensorId = entry.getKey();
            Condition condition = entry.getValue();
            SensorStateAvro sensorState = sensorStates.get(sensorId);

            if (sensorState == null) {
                log.debug("❌ Датчик {} не найден в снапшоте", sensorId);
                allConditionsMet = false;
                break;
            }

            if (!isConditionMet(condition, sensorState)) {
                log.debug("❌ Условие не выполнено для датчика {}", sensorId);
                allConditionsMet = false;
                break;
            }
        }

        // Если все условия выполнены - выполняем действия
        if (allConditionsMet) {
            log.info("🎯 Сценарий '{}' активирован!", scenario.getName());
            executeActions(scenario);
        } else {
            log.debug("⏸️ Сценарий '{}' не активирован", scenarioName);
        }
    }

    /**
     * Проверяет выполнено ли условие для датчика
     */
    private boolean isConditionMet(Condition condition, SensorStateAvro sensorState) {
        Integer actualValue = extractSensorValue(condition.getType(), sensorState);
        Integer expectedValue = condition.getValue();

        if (actualValue == null || expectedValue == null) {
            log.warn("⚠️ Не удалось проверить условие: actual={}, expected={}",
                    actualValue, expectedValue);
            return false;
        }

        // Выполняем сравнение в зависимости от операции
        boolean result = switch (condition.getOperation()) {
            case EQUALS -> actualValue.equals(expectedValue);
            case GREATER_THAN -> actualValue > expectedValue;
            case LOWER_THAN -> actualValue < expectedValue;
        };

        log.trace("🔍 Проверка условия: тип={}, операция={}, actual={}, expected={}, результат={}",
                condition.getType(), condition.getOperation(),
                actualValue, expectedValue, result);

        return result;
    }

    /**
     * Извлекает значение из датчика в зависимости от типа условия
     */
    private Integer extractSensorValue(ConditionType conditionType, SensorStateAvro sensorState) {
        Object sensorData = sensorState.getData();

        try {
            return switch (conditionType) {
                case MOTION -> {
                    if (sensorData instanceof MotionSensorAvro motionSensor) {
                        yield motionSensor.getMotion() ? 1 : 0;
                    }
                    yield null;
                }
                case TEMPERATURE -> {
                    if (sensorData instanceof TemperatureSensorAvro tempSensor) {
                        yield tempSensor.getTemperatureC();
                    }
                    if (sensorData instanceof ClimateSensorAvro climateSensor) {
                        yield climateSensor.getTemperatureC();
                    }
                    yield null;
                }
                case LUMINOSITY -> {
                    if (sensorData instanceof LightSensorAvro lightSensor) {
                        yield lightSensor.getLuminosity();
                    }
                    yield null;
                }
                case SWITCH -> {
                    if (sensorData instanceof SwitchSensorAvro switchSensor) {
                        yield switchSensor.getState() ? 1 : 0;
                    }
                    yield null;
                }
                case HUMIDITY -> {
                    if (sensorData instanceof ClimateSensorAvro climateSensor) {
                        yield climateSensor.getHumidity();
                    }
                    yield null;
                }
                case CO2LEVEL -> {
                    if (sensorData instanceof ClimateSensorAvro climateSensor) {
                        yield climateSensor.getCo2Level();
                    }
                    yield null;
                }
            };
        } catch (Exception e) {
            log.error("❌ Ошибка извлечения значения: тип={}, данные={}",
                    conditionType, sensorData.getClass().getSimpleName(), e);
            return null;
        }
    }

    /**
     * Выполняет все действия сценария
     */
    private void executeActions(Scenario scenario) {
        log.debug("⚡ Выполняю действия сценария '{}'", scenario.getName());

        for (Map.Entry<String, Action> entry : scenario.getActions().entrySet()) {
            String sensorId = entry.getKey();
            Action action = entry.getValue();

            try {
                // Создаем gRPC запрос ЧЕРЕЗ БИН
                var request = deviceActionRequestMapper.map(
                        scenario,
                        scenario.getHubId(),
                        sensorId,
                        action
                );

                // Отправляем команду через gRPC
                hubRouterClient.sendAction(request);
                log.info("📡 Команда отправлена: сценарий={}, датчик={}, действие={}",
                        scenario.getName(), sensorId, action.getType());

            } catch (Exception e) {
                log.error("❌ Ошибка выполнения действия: сценарий={}, датчик={}",
                        scenario.getName(), sensorId, e);
            }
        }

        log.info("✅ Все действия сценария '{}' выполнены", scenario.getName());
    }
}