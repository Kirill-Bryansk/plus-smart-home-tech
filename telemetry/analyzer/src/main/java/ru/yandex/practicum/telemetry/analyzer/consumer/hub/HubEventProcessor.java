package ru.yandex.practicum.telemetry.analyzer.consumer.hub;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.analyzer.consumer.base.BaseKafkaProcessor;
import ru.yandex.practicum.telemetry.analyzer.consumer.base.ProcessorProperties;
import ru.yandex.practicum.telemetry.analyzer.consumer.hub.handler.EventHandler;

import java.util.List;
import java.util.Properties;

/**
 * Процессор для обработки событий хаба (добавление/удаление устройств и сценариев)
 * Читает из топика telemetry.hubs.v1
 */
@Slf4j
@Component
public class HubEventProcessor extends BaseKafkaProcessor<HubEventAvro> {

    private final Properties hubConsumerProperties;
    private final List<EventHandler> handlers;

    /**
     * Конструктор для Spring Dependency Injection
     */
    public HubEventProcessor(ProcessorProperties properties,
                             Properties hubConsumerProperties,
                             List<EventHandler> handlers) {
        super(properties);
        this.hubConsumerProperties = hubConsumerProperties;
        this.handlers = handlers;
        log.info("✅ HubEventProcessor создан с {} обработчиками", handlers.size());
    }

    @Override
    protected Consumer<String, HubEventAvro> createConsumer() {
        log.debug("Создаю Kafka Consumer для HubEventProcessor");
        return new KafkaConsumer<>(hubConsumerProperties);
    }

    @Override
    protected void processRecord(HubEventAvro event) {
        String hubId = event.getHubId();
        Object payload = event.getPayload();
        String payloadType = payload.getClass().getSimpleName();

        log.debug("📋 Обрабатываю событие хаба: hubId={}, тип={}", hubId, payloadType);

        // Ищем подходящий обработчик
        for (EventHandler handler : handlers) {
            if (handler.canHandle(payload)) {
                log.trace("Найден обработчик {} для события {}",
                        handler.getClass().getSimpleName(), payloadType);
                handler.handle(hubId, payload);
                return;
            }
        }

        // Если обработчик не найден
        log.warn("⚠️ Не найден обработчик для типа события: {}", payloadType);
    }

    @Override
    protected String getTopic() {
        return "telemetry.hubs.v1";
    }

    @Override
    protected String getProcessorName() {
        return "HubEventProcessor";
    }

    /**
     * Дополнительный метод для логирования статистики
     */
    public void logStatistics() {
        log.info("📊 HubEventProcessor статистика: обработчиков={}", handlers.size());
        handlers.forEach(handler ->
                log.debug("  - {}", handler.getClass().getSimpleName()));
    }
}