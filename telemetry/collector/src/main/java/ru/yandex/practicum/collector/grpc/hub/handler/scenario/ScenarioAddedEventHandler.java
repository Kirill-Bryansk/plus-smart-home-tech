package ru.yandex.practicum.collector.grpc.hub.handler.scenario;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.grpc.hub.handler.BaseHubEventHandler;
import ru.yandex.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Обработчик события добавления сценария (SCENARIO_ADDED).
 * Обрабатывает сложную структуру сценария: условия и действия.
 */
@Slf4j
@Component("grpcScenarioAddedEventHandler")
public class ScenarioAddedEventHandler extends BaseHubEventHandler {

    public ScenarioAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
        log.debug("Инициализирован ScenarioAddedEventHandler");
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    protected SpecificRecordBase mapToAvro(HubEventProto event) {
        log.debug("Преобразование HubEventProto → HubEventAvro для SCENARIO_ADDED");

        ScenarioAddedEventProto scenarioEvent = event.getScenarioAdded();
        if (scenarioEvent == null) {
            log.error("Поле scenario_added не установлено в HubEventProto");
            throw new IllegalArgumentException("Отсутствуют данные о добавлении сценария");
        }

        log.debug("Данные сценария: name={}, условий={}, действий={}",
                scenarioEvent.getName(),
                scenarioEvent.getConditionCount(),
                scenarioEvent.getActionCount());

        // Преобразование условий сценария
        List<ScenarioConditionAvro> conditions = scenarioEvent.getConditionList().stream()
                .map(this::mapConditionToAvro)
                .collect(Collectors.toList());

        // Преобразование действий сценария
        List<DeviceActionAvro> actions = scenarioEvent.getActionList().stream()
                .map(this::mapActionToAvro)
                .collect(Collectors.toList());

        ScenarioAddedEventAvro scenarioAvro = ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioEvent.getName())
                .setConditions(conditions)
                .setActions(actions)
                .build();

        Instant timestamp = Instant.ofEpochSecond(
                event.getTimestamp().getSeconds(),
                event.getTimestamp().getNanos()
        );

        HubEventAvro hubEvent = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(timestamp)
                .setPayload(scenarioAvro)
                .build();

        log.debug("HubEventAvro создан: hubId={}, тип payload=ScenarioAddedEventAvro",
                event.getHubId());
        return hubEvent;
    }

    private ScenarioConditionAvro mapConditionToAvro(ScenarioConditionProto condition) {
        // Преобразование значения условия (int или bool)
        int conditionValue;
        if (condition.getValueCase() == ScenarioConditionProto.ValueCase.BOOL_VALUE) {
            conditionValue = condition.getBoolValue() ? 1 : 0;
        } else {
            conditionValue = condition.getIntValue();
        }

        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                .setValue(conditionValue)
                .build();
    }

    private DeviceActionAvro mapActionToAvro(DeviceActionProto action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(ActionTypeAvro.valueOf(action.getType().name()))
                .setValue(action.getValue())
                .build();
    }
}