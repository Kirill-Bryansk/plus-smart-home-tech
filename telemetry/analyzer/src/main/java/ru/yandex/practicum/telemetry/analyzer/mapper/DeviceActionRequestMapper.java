package ru.yandex.practicum.telemetry.analyzer.mapper;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.telemetry.analyzer.model.Action;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;

import java.time.Instant;

/**
 * Маппер для преобразования Action в gRPC сообщение
 */
@Slf4j
@Component
public class DeviceActionRequestMapper {

    /**
     * Создает gRPC запрос для выполнения действия
     */
    public DeviceActionRequest map(Scenario scenario, String hubId, String sensorId, Action action) {
        log.debug("📝 Создаю gRPC запрос: сценарий={}, датчик={}, действие={}",
                scenario.getName(), sensorId, action.getType());

        // Создаем прото действие
        DeviceActionProto.Builder actionBuilder = DeviceActionProto.newBuilder()
                .setSensorId(sensorId)
                .setType(mapActionType(action.getType()));

        // Добавляем значение если есть
        if (action.getValue() != null) {
            actionBuilder.setValue(action.getValue());
        }

        // Создаем полный запрос
        DeviceActionRequest request = DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenario.getName())
                .setAction(actionBuilder.build())
                .setTimestamp(getCurrentTimestamp())
                .build();

        log.trace("✅ gRPC запрос создан: {}", request);
        return request;
    }

    /**
     * Маппит внутренний ActionType в gRPC ActionTypeProto
     */
    private ActionTypeProto mapActionType(ru.yandex.practicum.telemetry.analyzer.model.enums.ActionType actionType) {
        try {
            return ActionTypeProto.valueOf(actionType.name());
        } catch (IllegalArgumentException e) {
            log.error("❌ Неизвестный тип действия: {}", actionType);
            throw new IllegalArgumentException("Неизвестный тип действия: " + actionType, e);
        }
    }

    /**
     * Создает текущую временную метку
     */
    private Timestamp getCurrentTimestamp() {
        Instant now = Instant.now();
        return Timestamp.newBuilder()
                .setSeconds(now.getEpochSecond())
                .setNanos(now.getNano())
                .build();
    }
}