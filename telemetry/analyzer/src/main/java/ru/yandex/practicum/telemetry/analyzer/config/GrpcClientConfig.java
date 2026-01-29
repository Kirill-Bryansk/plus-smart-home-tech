package ru.yandex.practicum.telemetry.analyzer.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

/**
 * Конфигурация gRPC клиента для Hub Router
 */
@Slf4j
@Configuration
public class GrpcClientConfig {

    @Value("${grpc.client.hub-router.address}")
    private String hubRouterAddress;

    @Value("${grpc.client.hub-router.enable-keep-alive:true}")
    private boolean enableKeepAlive;

    @Value("${grpc.client.hub-router.keep-alive-without-calls:true}")
    private boolean keepAliveWithoutCalls;

    /**
     * Создает управляемый канал для gRPC соединения
     */
    @Bean
    public ManagedChannel hubRouterChannel() {
        log.info("🔗 Создаю gRPC канал для Hub Router: {}", hubRouterAddress);

        // Извлекаем хост и порт из адреса (static://localhost:59090)
        String[] parts = hubRouterAddress.replace("static://", "").split(":");
        String host = parts[0];
        int port = Integer.parseInt(parts[1]);

        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext() // Без TLS для локальной разработки
                .enableRetry() // Включаем retry механизм
                .keepAliveWithoutCalls(keepAliveWithoutCalls)
                .build();

        log.info("✅ gRPC канал создан: {}:{}", host, port);
        return channel;
    }

    /**
     * Создает gRPC stub для вызова методов Hub Router
     */
    @Bean
    public HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub(ManagedChannel channel) {
        log.debug("🛠️ Создаю gRPC stub для Hub Router");
        HubRouterControllerGrpc.HubRouterControllerBlockingStub stub =
                HubRouterControllerGrpc.newBlockingStub(channel);
        log.info("✅ gRPC stub создан");
        return stub;
    }
}