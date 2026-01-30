package ru.yandex.practicum.telemetry.analyzer.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.telemetry.analyzer.consumer.base.ProcessorProperties;
import ru.yandex.practicum.telemetry.analyzer.deserializer.HubEventDeserializer;
import ru.yandex.practicum.telemetry.analyzer.deserializer.SensorsSnapshotDeserializer;

import java.util.Properties;

/**
 * Конфигурация Kafka Consumer для процессоров
 */
@Slf4j
@Configuration
public class KafkaConsumerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${analyzer.kafka.consumer.hubs.group-id}")
    private String hubsGroupId;

    @Value("${analyzer.kafka.consumer.snapshots.group-id}")
    private String snapshotsGroupId;

    /**
     * Конфигурация для HubEventProcessor
     */
    @Bean
    public Properties hubConsumerProperties(ProcessorProperties processorProps) {
        log.debug("Создаю конфигурацию Kafka Consumer для HubEventProcessor");

        Properties props = createBaseProperties(processorProps);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, hubsGroupId);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Читаем все события с начала

        log.info("✅ Конфигурация HubEventProcessor создана: groupId={}, топик=telemetry.hubs.v1",
                hubsGroupId);
        return props;
    }

    /**
     * Конфигурация для SnapshotProcessor
     */
    @Bean
    public Properties snapshotConsumerProperties(ProcessorProperties processorProps) {
        log.debug("Создаю конфигурацию Kafka Consumer для SnapshotProcessor");

        Properties props = createBaseProperties(processorProps);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, snapshotsGroupId);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorsSnapshotDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); // Только новые снапшоты

        log.info("✅ Конфигурация SnapshotProcessor создана: groupId={}, топик=telemetry.snapshots.v1",
                snapshotsGroupId);
        return props;
    }

    /**
     * Создает базовые свойства для всех потребителей
     */
    private Properties createBaseProperties(ProcessorProperties processorProps) {
        Properties props = new Properties();

        // Обязательные настройки
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, processorProps.isAutoCommit());

        // Настройки производительности и надежности
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, processorProps.getMaxPollRecords());
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, processorProps.getSessionTimeoutMs());
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, processorProps.getHeartbeatIntervalMs());
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, processorProps.getMaxPollIntervalMs());

        // Дополнительные настройки
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, processorProps.getAutoOffsetReset());
        props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, processorProps.isAllowAutoCreateTopics());

        return props;
    }
}