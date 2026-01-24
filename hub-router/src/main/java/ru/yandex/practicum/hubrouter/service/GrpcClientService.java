package ru.yandex.practicum.hubrouter.service;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Slf4j
@Service
public class GrpcClientService {

    @Value("${grpc.collector.host:localhost}")
    private String collectorHost;

    @Value("${grpc.collector.port:59091}")
    private int collectorPort;

    @Value("${hub.id:smart-home-hub-1}") // Добавляем ID хаба
    private String hubId;

    private ManagedChannel channel;
    private CollectorControllerGrpc.CollectorControllerBlockingStub blockingStub;

    @PostConstruct
    public void init() {
        channel = ManagedChannelBuilder.forAddress(collectorHost, collectorPort)
                .usePlaintext()
                .build();
        blockingStub = CollectorControllerGrpc.newBlockingStub(channel);
        log.info("gRPC клиент инициализирован: {}:{}, Hub ID: {}", collectorHost, collectorPort, hubId);
    }

    public void sendEvent(SensorEventProto event) {
        try {
            // Добавляем hubId к событию
            SensorEventProto eventWithHub = event.toBuilder()
                    .setHubId(hubId)
                    .build();

            Empty response = blockingStub.sendSensorEvent(eventWithHub);
            log.trace("Событие успешно отправлено: {}, hub: {}", event.getId(), hubId);
        } catch (Exception e) {
            log.error("Ошибка при отправке события {} (hub: {}): {}",
                    event.getId(), hubId, e.getMessage(), e);
        }
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
            log.info("gRPC канал закрыт");
        }
    }
}