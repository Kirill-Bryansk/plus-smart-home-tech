package ru.yandex.practicum.telemetry.analyzer.consumer.hub.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.telemetry.analyzer.consumer.hub.service.SensorService;
import ru.yandex.practicum.telemetry.analyzer.model.*;
import ru.yandex.practicum.telemetry.analyzer.model.enums.*;

/**
 * Маппер для преобразования Avro объектов в JPA сущности
 */
@Component
@RequiredArgsConstructor
public class AvroToEntityMapper {

    private final SensorService sensorService;

    /**
     * Преобразует Avro событие сценария в JPA сущность Scenario
     */
    public Scenario mapToScenario(ScenarioAddedEventAvro event, String hubId) {
        Scenario scenario = new Scenario();
        scenario.setHubId(hubId);
        scenario.setName(event.getName());

        // Маппим условия
        event.getConditions().forEach(conditionAvro -> {
            sensorService.ensureSensorExists(conditionAvro.getSensorId(), hubId);
            Condition condition = mapToCondition(conditionAvro);
            scenario.getConditions().put(conditionAvro.getSensorId(), condition);
        });

        // Маппим действия
        event.getActions().forEach(actionAvro -> {
            sensorService.ensureSensorExists(actionAvro.getSensorId(), hubId);
            Action action = mapToAction(actionAvro);
            scenario.getActions().put(actionAvro.getSensorId(), action);
        });

        return scenario;
    }

    /**
     * Преобразует Avro условие в JPA сущность Condition
     */
    public Condition mapToCondition(ScenarioConditionAvro conditionAvro) {
        Condition condition = new Condition();

        // Маппим enum типы
        condition.setType(ConditionType.valueOf(conditionAvro.getType().name()));
        condition.setOperation(ConditionOperation.valueOf(conditionAvro.getOperation().name()));

        // Маппим значение (union тип)
        Object value = conditionAvro.getValue();
        if (value instanceof Integer intValue) {
            condition.setValue(intValue);
        } else if (value instanceof Boolean boolValue) {
            // Конвертируем boolean в integer для хранения в БД
            condition.setValue(boolValue ? 1 : 0);
        }
        // null значения оставляем как есть

        return condition;
    }

    /**
     * Преобразует Avro действие в JPA сущность Action
     */
    public Action mapToAction(DeviceActionAvro actionAvro) {
        Action action = new Action();

        // Маппим enum тип
        action.setType(ActionType.valueOf(actionAvro.getType().name()));

        // Маппим значение (union тип)
        Object value = actionAvro.getValue();
        if (value instanceof Integer intValue) {
            action.setValue(intValue);
        }
        // null значения оставляем как есть

        return action;
    }
}