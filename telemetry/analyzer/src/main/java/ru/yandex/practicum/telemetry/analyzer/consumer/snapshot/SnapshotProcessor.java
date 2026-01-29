package ru.yandex.practicum.telemetry.analyzer.consumer.snapshot;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.consumer.base.BaseKafkaProcessor;
import ru.yandex.practicum.telemetry.analyzer.consumer.base.ProcessorProperties;
import ru.yandex.practicum.telemetry.analyzer.service.ScenarioEvaluationService;

import java.util.Properties;

/**
 * Процессор для обработки снапшотов состояний датчиков
 * Читает из топика telemetry.snapshots.v1
 */
@Slf4j
@Component
public class SnapshotProcessor extends BaseKafkaProcessor<SensorsSnapshotAvro> {

    private final Properties snapshotConsumerProperties;
    private final ScenarioEvaluationService scenarioService;

    /**
     * Конструктор для Spring Dependency Injection
     */
    public SnapshotProcessor(ProcessorProperties properties,
                             Properties snapshotConsumerProperties,
                             ScenarioEvaluationService scenarioService) {
        super(properties);
        this.snapshotConsumerProperties = snapshotConsumerProperties;
        this.scenarioService = scenarioService;
        log.info("✅ SnapshotProcessor создан");
    }

    @Override
    protected Consumer<String, SensorsSnapshotAvro> createConsumer() {
        log.debug("Создаю Kafka Consumer для SnapshotProcessor");
        return new KafkaConsumer<>(snapshotConsumerProperties);
    }

    @Override
    protected void processRecord(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        int sensorCount = snapshot.getSensorsState().size();

        log.debug("📊 Обрабатываю снапшот: hubId={}, датчиков={}", hubId, sensorCount);

        try {
            // Передаем снапшот в сервис для анализа сценариев
            scenarioService.evaluateAndExecute(snapshot);
            log.debug("✅ Снапшот обработан: hubId={}", hubId);
        } catch (Exception e) {
            log.error("❌ Ошибка при анализе снапшота hubId={}: {}", hubId, e.getMessage(), e);
            throw e; // Пробрасываем дальше для обработки в базовом классе
        }
    }

    @Override
    protected String getTopic() {
        return "telemetry.snapshots.v1";
    }

    @Override
    protected String getProcessorName() {
        return "SnapshotProcessor";
    }
}