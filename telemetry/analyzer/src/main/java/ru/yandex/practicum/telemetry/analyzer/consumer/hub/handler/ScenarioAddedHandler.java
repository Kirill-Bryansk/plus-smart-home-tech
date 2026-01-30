package ru.yandex.practicum.telemetry.analyzer.consumer.hub.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.telemetry.analyzer.consumer.hub.mapper.AvroToEntityMapper;
import ru.yandex.practicum.telemetry.analyzer.consumer.hub.service.SensorService;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;

/**
 * Обработчик события добавления сценария
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedHandler implements EventHandler {

    private final ScenarioRepository scenarioRepository;
    private final SensorService sensorService;
    private final AvroToEntityMapper mapper;

    @Override
    public boolean canHandle(Object payload) {
        return payload instanceof ScenarioAddedEventAvro;
    }

    @Override
    @Transactional
    public void handle(String hubId, Object payload) {
        ScenarioAddedEventAvro event = (ScenarioAddedEventAvro) payload;
        String scenarioName = event.getName();

        log.debug("📝 Обрабатываю добавление сценария: hubId={}, name={}", hubId, scenarioName);

        // Удаляем старый сценарий с таким же именем (если есть)
        scenarioRepository.findByHubIdAndName(hubId, scenarioName)
                .ifPresent(existing -> {
                    scenarioRepository.delete(existing);
                    log.debug("🗑️ Удален существующий сценарий: name={}", scenarioName);
                });

        // Создаем новый сценарий через маппер
        Scenario scenario = mapper.mapToScenario(event, hubId);
        scenarioRepository.save(scenario);

        log.info("✅ Сценарий добавлен: name={}, hubId={}, условий={}, действий={}",
                scenarioName, hubId, event.getConditions().size(), event.getActions().size());
    }
}