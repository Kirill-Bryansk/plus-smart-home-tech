package ru.yandex.practicum.telemetry.analyzer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class GrpcClientConfig {
    // Конфигурация через аннотации (@GrpcClient) в application.yml
    // Spring Boot автоматически создаст клиент
}