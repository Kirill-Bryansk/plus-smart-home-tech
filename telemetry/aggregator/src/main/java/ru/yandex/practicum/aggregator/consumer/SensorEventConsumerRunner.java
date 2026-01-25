package ru.yandex.practicum.aggregator.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Duration;
import java.util.List;

/**
 * Основной цикл обработки сообщений из Kafka.
 * Запускает poll loop для чтения событий датчиков.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SensorEventConsumerRunner {

    private final SensorEventHandler eventHandler;
    private final KafkaConsumer<String, SensorEventAvro> consumer;

    // Топик для подписки
    private static final String TOPIC = "telemetry.sensors.v1";
    // Таймаут для poll
    private static final Duration POLL_TIMEOUT = Duration.ofMillis(100);

    /**
     * Запускает основной цикл обработки сообщений.
     * Подписывается на топик и начинает poll loop.
     */
    public void start() {
        log.info("Запуск обработки событий из топика {}", TOPIC);

        // Добавляем hook для корректного завершения
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Получен сигнал завершения, прерываю работу consumer");
            consumer.wakeup();
        }));

        try {
            // Подписываемся на топик
            consumer.subscribe(List.of(TOPIC));
            log.debug("Подписался на топик {}", TOPIC);

            // Основной poll loop
            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(POLL_TIMEOUT);

                if (!records.isEmpty()) {
                    log.trace("Получено {} сообщений для обработки", records.count());
                    eventHandler.handle(records);
                }
            }

        } catch (WakeupException e) {
            // Игнорируем - это нормальное завершение работы
            log.debug("Consumer был прерван для завершения работы");
        } finally {
            // Корректно завершаем работу
            eventHandler.shutdown();
            log.info("Обработчик событий завершил работу");
        }
    }
}