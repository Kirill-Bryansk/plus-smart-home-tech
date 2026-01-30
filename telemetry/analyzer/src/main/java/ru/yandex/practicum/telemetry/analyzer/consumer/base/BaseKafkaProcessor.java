package ru.yandex.practicum.telemetry.analyzer.consumer.base;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;

/**
 * Базовый класс для всех Kafka процессоров.
 * Реализует общую логику чтения из Kafka.
 */
@Slf4j
public abstract class BaseKafkaProcessor<T> implements Runnable {

    protected final ProcessorProperties properties;
    private volatile boolean running = true;

    /**
     * Конструктор для Spring/Lombok
     * Нужен для корректного создания наследников
     */
    protected BaseKafkaProcessor(ProcessorProperties properties) {
        this.properties = properties;
        log.debug("Инициализирован {} с настройками: {}", getProcessorName(), properties);
    }

    /**
     * Создает Kafka Consumer для конкретного типа данных
     */
    protected abstract Consumer<String, T> createConsumer();

    /**
     * Обрабатывает одну запись из Kafka
     */
    protected abstract void processRecord(T record);

    /**
     * Возвращает имя топика Kafka для подписки
     */
    protected abstract String getTopic();

    /**
     * Возвращает имя процессора для логирования
     */
    protected abstract String getProcessorName();

    @Override
    public void run() {
        log.info("🚀 Запускаем {} для топика {}", getProcessorName(), getTopic());

        try (Consumer<String, T> consumer = createConsumer()) {
            consumer.subscribe(Collections.singletonList(getTopic()));
            log.info("✅ {} подписался на топик: {}", getProcessorName(), getTopic());

            while (running) {
                try {
                    ConsumerRecords<String, T> records = consumer.poll(Duration.ofMillis(properties.getPollTimeout()));

                    if (!records.isEmpty()) {
                        log.debug("📥 {} получил {} записей", getProcessorName(), records.count());
                        records.forEach(record -> {
                            try {
                                processRecord(record.value());
                            } catch (Exception e) {
                                log.error("❌ {}: ошибка обработки записи", getProcessorName(), e);
                            }
                        });
                        consumer.commitSync();
                        log.debug("✅ {}: смещения зафиксированы", getProcessorName());
                    }
                } catch (Exception e) {
                    log.error("❌ {}: ошибка при чтении из Kafka", getProcessorName(), e);
                }
            }
        } catch (Exception e) {
            log.error("❌ {}: критическая ошибка", getProcessorName(), e);
        } finally {
            log.info("🛑 {} остановлен", getProcessorName());
        }
    }

    /**
     * Останавливает процессор
     */
    public void stop() {
        log.info("🛑 Останавливаю {}...", getProcessorName());
        running = false;
    }
}