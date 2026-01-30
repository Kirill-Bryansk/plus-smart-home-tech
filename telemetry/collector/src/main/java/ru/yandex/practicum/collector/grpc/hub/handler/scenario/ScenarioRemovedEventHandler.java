package ru.yandex.practicum.collector.grpc.hub.handler.scenario;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.grpc.hub.handler.BaseHubEventHandler;
import ru.yandex.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.time.Instant;

/**
 * Обработчик события удаления сценария (SCENARIO_REMOVED).
 * Самая простая трансформация — только имя сценария.
 */
@Slf4j
@Component("grpcScenarioRemovedEventHandler")
public class ScenarioRemovedEventHandler extends BaseHubEventHandler {

    public ScenarioRemovedEventHandler(KafkaEventProducer producer) {
        super(producer);
        log.debug("Инициализирован ScenarioRemovedEventHandler");
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    protected SpecificRecordBase mapToAvro(HubEventProto event) {
        log.debug("Преобразование HubEventProto → HubEventAvro для SCENARIO_REMOVED");

        ScenarioRemovedEventProto scenarioEvent = event.getScenarioRemoved();

        ScenarioRemovedEventAvro scenarioAvro = ScenarioRemovedEventAvro.newBuilder()
                .setName(scenarioEvent.getName())
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

        log.debug("HubEventAvro создан: hubId={}, тип payload=ScenarioRemovedEventAvro",
                event.getHubId());
        return hubEvent;
    }
}