package ru.yandex.practicum.collector.grpc;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
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

    @Override
    public void sendSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        log.info("╔═══════════════════════════════════════════════════════════════╗");
        log.info("║ gRPC ВХОДЯЩИЙ ЗАПРОС: sendSensorEvent                         ║");
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
    public void sendHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        log.info("╔═══════════════════════════════════════════════════════════════╗");
        log.info("║ gRPC ВХОДЯЩИЙ ЗАПРОС: sendHubEvent                            ║");
        log.info("║ Hub ID: {}                                           ║", request.getHubId());
        log.info("╚═══════════════════════════════════════════════════════════════╝");

        try {
            // TODO: Реализовать обработку событий хаба
            log.warn("Обработка событий хаба пока не реализована");

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

            log.info("✓ Событие хаба {} принято (обработка в разработке)", request.getHubId());

        } catch (Exception e) {
            log.error("Ошибка обработки события хаба", e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription(e.getMessage())
            ));
        } finally {
            log.info("─────────────────────────────────────────────────────────────");
        }
    }
}