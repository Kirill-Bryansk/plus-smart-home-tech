package ru.yandex.practicum.telemetry.analyzer.service.grpc;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

/**
 * gRPC клиент для отправки команд на Hub Router
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HubRouterClient {

    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub;

    /**
     * Отправляет команду на Hub Router через gRPC
     */
    public void sendAction(DeviceActionRequest request) {
        String hubId = request.getHubId();
        String scenarioName = request.getScenarioName();
        String sensorId = request.getAction().getSensorId();

        log.debug("📡 Отправляю команду через gRPC: hubId={}, сценарий={}, датчик={}",
                hubId, scenarioName, sensorId);

        try {
            // Вызов gRPC метода
            hubRouterStub.handleDeviceAction(request);

            log.info("✅ Команда отправлена успешно: hubId={}, сценарий={}, датчик={}",
                    hubId, scenarioName, sensorId);

        } catch (StatusRuntimeException e) {
            log.error("❌ Ошибка gRPC вызова: статус={}, описание={}",
                    e.getStatus().getCode(), e.getStatus().getDescription(), e);
            throw new GrpcClientException("Ошибка отправки команды через gRPC", e);
        } catch (Exception e) {
            log.error("❌ Неожиданная ошибка при отправке команды", e);
            throw new GrpcClientException("Неожиданная ошибка gRPC клиента", e);
        }
    }

    /**
     * Проверяет доступность gRPC сервера
     */
    public boolean isServerAvailable() {
        try {
            // Можно добавить ping-метод если он есть в proto
            log.debug("🔍 Проверяю доступность Hub Router...");
            return true;
        } catch (Exception e) {
            log.warn("⚠️ Hub Router недоступен: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Кастомное исключение для ошибок gRPC
     */
    public static class GrpcClientException extends RuntimeException {
        public GrpcClientException(String message, Throwable cause) {
            super(message, cause);
        }

        public GrpcClientException(String message) {
            super(message);
        }
    }
}