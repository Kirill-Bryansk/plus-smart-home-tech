package ru.yandex.practicum.aggregator.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.aggregator.producer.SensorsSnapshotProducer;
import ru.yandex.practicum.aggregator.service.AggregationService;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.HashMap;
import java.util.Map;

/**
 * Обработчик событий от датчиков.
 * Обрабатывает batch сообщений из Kafka и управляет оффсетами.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SensorEventHandler {

    private final AggregationService aggregationService;
    private final SensorsSnapshotProducer producer;
    private final KafkaConsumer<String, SensorEventAvro> consumer;

    /**
     * Хранит текущие оффсеты для каждой партиции топика.
     * Ключ: TopicPartition, Значение: OffsetAndMetadata
     */
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    /**
     * Обрабатывает batch сообщений из Kafka.
     * Для каждого события вызывает агрегацию и отправляет снапшот при изменении.
     *
     * @param records batch сообщений для обработки
     */
    public void handle(ConsumerRecords<String, SensorEventAvro> records) {
        log.debug("Получено {} сообщений для обработки", records.count());

        for (ConsumerRecord<String, SensorEventAvro> record : records) {
            // Агрегируем событие
            aggregationService.aggregateEvent(record.value())
                    .ifPresent(snapshot -> {
                        // Отправляем снапшот если состояние изменилось
                        producer.send("telemetry.snapshots.v1", snapshot.getHubId(), snapshot);
                    });

            // Сохраняем оффсет для фиксации
            currentOffsets.put(
                    new TopicPartition(record.topic(), record.partition()),
                    new OffsetAndMetadata(record.offset() + 1)
            );
        }

        // Фиксируем оффсеты
        commitOffsets();
    }

    /**
     * Асинхронно фиксирует обработанные оффсеты в Kafka.
     */
    private void commitOffsets() {
        consumer.commitAsync(currentOffsets, (offsets, exception) -> {
            if (exception != null) {
                log.error("Ошибка при фиксации оффсетов", exception);
            } else {
                log.trace("Оффсеты успешно зафиксированы");
            }
        });
    }

    /**
     * Корректное завершение работы.
     * Синхронно фиксирует оффсеты и закрывает consumer.
     */
    public void shutdown() {
        log.info("Завершение работы SensorEventHandler");
        consumer.commitSync(currentOffsets);
        consumer.close();
        log.debug("Consumer закрыт");
    }
}