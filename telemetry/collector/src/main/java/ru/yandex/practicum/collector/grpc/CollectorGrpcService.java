package ru.yandex.practicum.collector.grpc;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.collector.grpc.hub.processor.GrpcHubProcessor;
import ru.yandex.practicum.collector.grpc.sensor.processor.GrpcSensorProcessor;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

/**
 * gRPC сервис для приема событий от датчиков и хабов.
 * Отвечает только за транспортный уровень: прием/отправка сообщений,
 * обработка gRPC специфичных ошибок, логирование входящих запросов.
 * Бизнес-логика делегируется GrpcSensorProcessor.
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CollectorGrpcService extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final GrpcSensorProcessor sensorProcessor;
    private final GrpcHubProcessor hubProcessor;

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        log.info("╔═══════════════════════════════════════════════════════════════╗");
        log.info("║ gRPC ВХОДЯЩИЙ ЗАПРОС: CollectSensorEvent                      ║");
        log.info("║ ID: {}, Hub: {}, Тип: {}                           ║",
                request.getId(),
                request.getHubId(),
                request.getPayloadCase());
        log.info("╚═══════════════════════════════════════════════════════════════╝");

        try {
            // Делегируем бизнес-логику процессору
            log.debug("Делегирую обработку события в GrpcSensorProcessor");
            sensorProcessor.processSensorEvent(request);

            // Отправляем успешный ответ
            log.info("Отправляю успешный ответ клиенту");
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

            log.info("✓ Обработка события {} завершена успешно", request.getId());

        } catch (IllegalArgumentException e) {
            // Клиентская ошибка (неверный формат, тип и т.д.)
            log.warn("КЛИЕНТСКАЯ ОШИБКА: {}", e.getMessage());
            responseObserver.onError(new StatusRuntimeException(
                    Status.INVALID_ARGUMENT.withDescription(e.getMessage())
            ));
        } catch (Exception e) {
            // Серверная ошибка
            log.error("СЕРВЕРНАЯ ОШИБКА обработки события: {}", e.getMessage(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Внутренняя ошибка сервера")
            ));
        } finally {
            log.info("─────────────────────────────────────────────────────────────");
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        log.info("╔═══════════════════════════════════════════════════════════════╗");
        log.info("║ gRPC ВХОДЯЩИЙ ЗАПРОС: CollectHubEvent                         ║");
        log.info("║ Hub ID: {}, Тип: {}                                ║",
                request.getHubId(), request.getPayloadCase());
        log.info("╚═══════════════════════════════════════════════════════════════╝");

        try {
            // Делегируем обработку процессору
            hubProcessor.processHubEvent(request);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

            log.info("✓ Hub-событие {} успешно обработано", request.getPayloadCase());
        } catch (IllegalArgumentException e) {
            log.warn("КЛИЕНТСКАЯ ОШИБКА: {}", e.getMessage());
            responseObserver.onError(new StatusRuntimeException(
                    Status.INVALID_ARGUMENT.withDescription(e.getMessage())
            ));
        } catch (Exception e) {
            log.error("СЕРВЕРНАЯ ОШИБКА обработки Hub-события: {}", e.getMessage(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription("Внутренняя ошибка сервера")
            ));
        } finally {
            log.info("─────────────────────────────────────────────────────────────");
        }
    }
}