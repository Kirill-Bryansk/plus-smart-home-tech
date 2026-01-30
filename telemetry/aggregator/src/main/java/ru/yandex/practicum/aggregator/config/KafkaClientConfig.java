package ru.yandex.practicum.aggregator.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

/**
 * Конфигурация Kafka клиентов - Consumer и Producer.
 * Создает бины для работы с Kafka.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaClientConfig {

    private final KafkaPropertiesConfig kafkaProperties;

    /**
     * Создает Kafka Consumer для чтения событий датчиков.
     * @return настроенный Consumer для SensorEventAvro
     */
    @Bean
    public KafkaConsumer<String, SensorEventAvro> kafkaConsumer() {
        log.debug("Создание Kafka Consumer для SensorEventAvro");
        Properties props = new Properties();
        props.putAll(kafkaProperties.getConsumer());
        return new KafkaConsumer<>(props);
    }

    /**
     * Создает Kafka Producer для отправки снапшотов.
     * @return настроенный Producer для SensorsSnapshotAvro
     */
    @Bean
    public Producer<String, SensorsSnapshotAvro> kafkaProducer() {
        log.debug("Создание Kafka Producer для SensorsSnapshotAvro");
        Properties props = new Properties();
        props.putAll(kafkaProperties.getProducer());
        return new KafkaProducer<>(props);
    }
}