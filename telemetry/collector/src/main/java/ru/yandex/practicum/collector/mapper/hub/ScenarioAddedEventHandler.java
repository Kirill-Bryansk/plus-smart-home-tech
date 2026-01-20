package ru.yandex.practicum.collector.mapper.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.hub.HubEventDto;
import ru.yandex.practicum.collector.dto.hub.device.DeviceAction;
import ru.yandex.practicum.collector.dto.hub.scenario.ScenarioAddedEventDto;
import ru.yandex.practicum.collector.dto.hub.scenario.ScenarioCondition;
import ru.yandex.practicum.collector.enums.HubEventType;
import ru.yandex.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {

    public ScenarioAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected HubEventType getSupportedType() {
        return HubEventType.SCENARIO_ADDED;
    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(HubEventDto event) {
        ScenarioAddedEventDto dto = (ScenarioAddedEventDto) event;

        List<ScenarioConditionAvro> conditions = dto.getConditions().stream()
                .map(this::mapCondition)
                .collect(Collectors.toList());

        List<DeviceActionAvro> actions = dto.getActions().stream()
                .map(this::mapAction)
                .collect(Collectors.toList());

        return ScenarioAddedEventAvro.newBuilder()
                .setName(dto.getName())
                .setConditions(conditions)
                .setActions(actions)
                .build();
    }

    private ScenarioConditionAvro mapCondition(ScenarioCondition condition) {
        // Используем valueOf() вместо switch
        ConditionTypeAvro conditionType = ConditionTypeAvro.valueOf(condition.getType().name());
        ConditionOperationAvro operation = ConditionOperationAvro.valueOf(condition.getOperation().name());

        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(conditionType)
                .setOperation(operation)
                .setValue(condition.getValue()) // Integer может быть null
                .build();
    }

    private DeviceActionAvro mapAction(DeviceAction action) {
        // Используем valueOf() вместо switch
        ActionTypeAvro actionType = ActionTypeAvro.valueOf(action.getType().name());

        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(actionType)
                .setValue(action.getValue()) // Integer может быть null
                .build();
    }
}