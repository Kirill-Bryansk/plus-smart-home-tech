package ru.yandex.practicum.telemetry.analyzer.config;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${analyzer.kafka.consumer.hubs.group-id}")
    private String hubsGroupId;

    @Value("${analyzer.kafka.consumer.snapshots.group-id}")
    private String snapshotsGroupId;

    @Bean
    public Properties hubConsumerProperties() {
        log.debug("Создаю конфигурацию Kafka потребителя для hub-событий");
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, hubsGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Читаем с начала
        log.debug("Конфигурация hubConsumer создана: groupId={}, servers={}", hubsGroupId, bootstrapServers);
        return props;
    }

    @Bean
    public Properties snapshotConsumerProperties() {
        log.debug("Создаю конфигурацию Kafka потребителя для снапшотов");
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, snapshotsGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); // Только новые сообщения
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // Ручная фиксация смещений
        log.debug("Конфигурация snapshotConsumer создана: groupId={}, autoCommit=false", snapshotsGroupId);
        return props;
    }
}