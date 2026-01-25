package ru.yandex.practicum.aggregator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Конфигурация Kafka свойств из application.yaml.
 * Загружает настройки для Consumer и Producer.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aggregator.kafka")
public class KafkaPropertiesConfig {

    /**
     * Настройки для Kafka Consumer.
     * Ключ-значения из application.yaml под aggregator.kafka.consumer
     */
    private Map<String, String> consumer;

    /**
     * Настройки для Kafka Producer.
     * Ключ-значения из application.yaml под aggregator.kafka.producer
     */
    private Map<String, String> producer;
}