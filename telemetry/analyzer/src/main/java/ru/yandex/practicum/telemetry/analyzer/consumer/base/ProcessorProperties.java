package ru.yandex.practicum.telemetry.analyzer.consumer.base;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки для Kafka процессоров.
 * Загружаются из application.yml с префиксом analyzer.kafka.processor
 */
@Data
@ConfigurationProperties(prefix = "analyzer.kafka.processor")
public class ProcessorProperties {

    /**
     * Таймаут опроса Kafka в миллисекундах
     */
    private int pollTimeout = 1000;

    /**
     * Автоматическое подтверждение смещений
     */
    private boolean autoCommit = false;

    /**
     * Стратегия при отсутствии смещения: earliest, latest, none
     */
    private String autoOffsetReset = "earliest";

    /**
     * Максимальное количество записей за один poll
     */
    private int maxPollRecords = 500;

    /**
     * Таймаут сессии потребителя в миллисекундах
     */
    private int sessionTimeoutMs = 10000;

    /**
     * Интервал heartbeat в миллисекундах
     */
    private int heartbeatIntervalMs = 3000;

    /**
     * Максимальный интервал между poll в миллисекундах
     */
    private int maxPollIntervalMs = 300000;

    /**
     * Включение автоматического создания топиков
     */
    private boolean allowAutoCreateTopics = false;

    /**
     * Проверяет корректность настроек
     */
    public void validate() {
        if (pollTimeout <= 0) {
            throw new IllegalArgumentException("pollTimeout должен быть положительным числом");
        }
        if (maxPollRecords <= 0) {
            throw new IllegalArgumentException("maxPollRecords должен быть положительным числом");
        }
        if (!autoOffsetReset.equals("earliest") && !autoOffsetReset.equals("latest") && !autoOffsetReset.equals("none")) {
            throw new IllegalArgumentException("autoOffsetReset должен быть earliest, latest или none");
        }
    }
}