package ru.yandex.practicum.telemetry.analyzer.consumer.hub.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;

/**
 * Обработчик события удаления сценария
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioRemovedHandler implements EventHandler {

    private final ScenarioRepository scenarioRepository;

    @Override
    public boolean canHandle(Object payload) {
        return payload instanceof ScenarioRemovedEventAvro;
    }

    @Override
    public void handle(String hubId, Object payload) {
        ScenarioRemovedEventAvro event = (ScenarioRemovedEventAvro) payload;
        String scenarioName = event.getName();

        log.debug("🗑️ Обрабатываю удаление сценария: hubId={}, name={}", hubId, scenarioName);

        // Ищем и удаляем сценарий
        scenarioRepository.findByHubIdAndName(hubId, scenarioName)
                .ifPresentOrElse(
                        scenario -> {
                            scenarioRepository.delete(scenario);
                            log.info("✅ Сценарий удален: name={}, hubId={}", scenarioName, hubId);
                        },
                        () -> log.warn("⚠️ Сценарий не найден для удаления: name={}, hubId={}",
                                scenarioName, hubId)
                );
    }
}