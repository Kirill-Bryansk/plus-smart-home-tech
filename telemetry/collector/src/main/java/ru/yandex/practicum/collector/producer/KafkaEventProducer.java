package ru.yandex.practicum.collector.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.config.KafkaConfig;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

import java.util.HashMap;
import java.util.Properties;

@Slf4j
@Component
public class KafkaEventProducer implements AutoCloseable {

    private static final String SENSORS_TOPIC = "telemetry.sensors.v1";
    private static final String HUBS_TOPIC = "telemetry.hubs.v1";
    private static final Duration SHUTDOWN_TIMEOUT = Duration.ofSeconds(10);

    private final KafkaProducer<String, SpecificRecordBase> producer;
    private final EnumMap<KafkaConfig.TopicType, String> topics;

    /**
     * Конструктор компонента.
     *
     * @param kafkaConfig конфигурация Kafka
     */
    public KafkaEventProducer(KafkaConfig kafkaConfig) {
        log.info("Инициализация KafkaEventProducer...");

        this.topics = initializeTopics();
        log.debug("Топики Kafka: {}", topics);

        // Получаем Properties и преобразуем в Map<String, Object>
        Properties producerProperties = kafkaConfig.getProducer().getProperties();
        Map<String, Object> configMap = propertiesToMap(producerProperties);

        validateProducerProperties(configMap);

        this.producer = new KafkaProducer<>(configMap);
        log.info("KafkaEventProducer успешно инициализирован");
    }

    /**
     * Преобразование Properties в Map<String, Object>.
     *
     * @param properties объект Properties
     * @return Map<String, Object> с настройками
     */
    private Map<String, Object> propertiesToMap(Properties properties) {
        Map<String, Object> map = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            map.put(key, properties.getProperty(key));
        }
        return map;
    }

    /**
     * Инициализация маппинга типов событий на топики Kafka.
     *
     * @return маппинг типов событий на имена топиков
     */
    private EnumMap<KafkaConfig.TopicType, String> initializeTopics() {
        EnumMap<KafkaConfig.TopicType, String> topicMap = new EnumMap<>(KafkaConfig.TopicType.class);
        topicMap.put(KafkaConfig.TopicType.SENSORS_EVENTS, SENSORS_TOPIC);
        topicMap.put(KafkaConfig.TopicType.HUBS_EVENTS, HUBS_TOPIC);
        return topicMap;
    }

    /**
     * Валидация настроек продюсера Kafka.
     *
     * @param properties настройки продюсера
     * @throws IllegalArgumentException если настройки невалидны
     */
    private void validateProducerProperties(Map<String, Object> properties) {
        if (properties == null || properties.isEmpty()) {
            throw new IllegalArgumentException("Конфигурация Kafka продюсера не может быть пустой");
        }

        if (!properties.containsKey("bootstrap.servers")) {
            throw new IllegalArgumentException("Отсутствует обязательный параметр: bootstrap.servers");
        }

        log.debug("Проверка настроек Kafka продюсера завершена успешно");
    }

    /**
     * Отправка события в Kafka.
     *
     * @param event событие для отправки
     * @param hubId идентификатор хаба
     * @param timestamp метка времени события
     * @param topicType тип топика для отправки
     */
    public void send(SpecificRecordBase event, String hubId, Instant timestamp,
                     KafkaConfig.TopicType topicType) {

        validateInputParameters(event, hubId, topicType);

        String topic = topics.get(topicType);
        if (topic == null) {
            log.error("Топик не найден для типа события: {}", topicType);
            throw new IllegalArgumentException("Неизвестный тип топика: " + topicType);
        }

        log.debug("Подготовка к отправке события в топик: {}, хаб: {}, тип события: {}",
                topic, hubId, event.getClass().getSimpleName());

        ProducerRecord<String, SpecificRecordBase> record = createProducerRecord(
                topic, hubId, event, timestamp
        );

        sendToKafkaWithCallback(record, topic, hubId);
    }

    /**
     * Валидация входных параметров.
     */
    private void validateInputParameters(SpecificRecordBase event, String hubId,
                                         KafkaConfig.TopicType topicType) {
        if (event == null) {
            throw new IllegalArgumentException("Событие не может быть null");
        }
        if (hubId == null || hubId.isBlank()) {
            throw new IllegalArgumentException("Идентификатор хаба не может быть пустым");
        }
        if (topicType == null) {
            throw new IllegalArgumentException("Тип топика не может быть null");
        }
    }

    /**
     * Создание ProducerRecord для отправки в Kafka.
     */
    private ProducerRecord<String, SpecificRecordBase> createProducerRecord(
            String topic, String hubId, SpecificRecordBase event, Instant timestamp) {

        return new ProducerRecord<>(
                topic,
                null, // partition (null для автоматического выбора)
                timestamp != null ? timestamp.toEpochMilli() : System.currentTimeMillis(),
                hubId,
                event
        );
    }

    /**
     * Отправка записи в Kafka с обработкой результата.
     */
    private void sendToKafkaWithCallback(ProducerRecord<String, SpecificRecordBase> record,
                                         String topic, String hubId) {

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                handleSendFailure(exception, topic, hubId);
            } else {
                handleSendSuccess(metadata, topic, hubId);
            }
        });

        // Гарантируем отправку сообщения
        producer.flush();
        log.debug("Событие отправлено в Kafka, ожидание подтверждения...");
    }

    /**
     * Обработка успешной отправки сообщения.
     */
    private void handleSendSuccess(RecordMetadata metadata, String topic, String hubId) {
        log.info("Событие успешно доставлено в Kafka - Топик: {}, Партиция: {}, Offset: {}, Хаб: {}",
                metadata.topic(),
                metadata.partition(),
                metadata.offset(),
                hubId);
    }

    /**
     * Обработка ошибки при отправке сообщения.
     */
    private void handleSendFailure(Exception exception, String topic, String hubId) {
        log.error("Ошибка при отправке события в Kafka - Топик: {}, Хаб: {}, Ошибка: {}",
                topic,
                hubId,
                exception.getMessage(),
                exception);
    }

    /**
     * Корректное закрытие ресурсов продюсера.
     */
    @Override
    public void close() {
        if (producer != null) {
            try {
                log.info("Завершение работы KafkaEventProducer...");
                producer.flush();
                producer.close(SHUTDOWN_TIMEOUT);
                log.info("KafkaEventProducer успешно завершил работу");
            } catch (Exception e) {
                log.warn("Ошибка при завершении работы KafkaEventProducer: {}", e.getMessage());
            }
        }
    }
}