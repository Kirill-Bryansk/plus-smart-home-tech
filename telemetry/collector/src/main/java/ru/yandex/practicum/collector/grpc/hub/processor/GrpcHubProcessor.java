package ru.yandex.practicum.collector.grpc.hub.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.grpc.hub.service.HubEventHandler;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Процессор обработки Hub-событий. Находит нужный обработчик по типу события.
 */
@Slf4j
@Service
public class GrpcHubProcessor {

    private final Map<HubEventProto.PayloadCase, HubEventHandler> handlers;

    /**
     * Конструктор создает мапу обработчиков для быстрого поиска по типу события.
     */
    public GrpcHubProcessor(List<HubEventHandler> hubHandlers) {
        log.info("Инициализация GrpcHubProcessor. Обработчиков: {}", hubHandlers.size());

        this.handlers = hubHandlers.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getMessageType,
                        Function.identity(),
                        (existing, replacement) -> {
                            String errorMsg = String.format(
                                    "Дублирующиеся обработчики для типа %s: %s и %s",
                                    existing.getMessageType(),
                                    existing.getClass().getSimpleName(),
                                    replacement.getClass().getSimpleName()
                            );
                            log.error(errorMsg);
                            throw new IllegalStateException(errorMsg);
                        }
                ));

        log.info("GrpcHubProcessor инициализирован. Типов событий: {}", handlers.size());
    }

    /**
     * Основной метод обработки Hub-события.
     */
    public void processHubEvent(HubEventProto event) {
        log.info("Обработка Hub-события. Hub ID: {}", event.getHubId());

        try {
            validateEvent(event);

            HubEventProto.PayloadCase eventType = event.getPayloadCase();
            HubEventHandler handler = findHandler(eventType);

            log.info("Обработчик для {}: {}", eventType, handler.getClass().getSimpleName());
            handler.handle(event);

            log.info("Hub-событие обработано. Тип: {}, Hub ID: {}", eventType, event.getHubId());
        } catch (IllegalArgumentException e) {
            log.warn("Ошибка валидации: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Ошибка обработки Hub-события: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка обработки Hub-события: " + e.getMessage(), e);
        }
    }

    private void validateEvent(HubEventProto event) {
        if (event == null) {
            throw new IllegalArgumentException("HubEventProto не может быть null");
        }
        if (event.getPayloadCase() == HubEventProto.PayloadCase.PAYLOAD_NOT_SET) {
            throw new IllegalArgumentException("Тип Hub-события не определен");
        }
        if (event.getHubId() == null || event.getHubId().isBlank()) {
            throw new IllegalArgumentException("Идентификатор хаба не может быть пустым");
        }
    }

    private HubEventHandler findHandler(HubEventProto.PayloadCase eventType) {
        HubEventHandler handler = handlers.get(eventType);
        if (handler == null) {
            String errorMsg = String.format(
                    "Не найден обработчик для типа: %s. Доступные типы: %s",
                    eventType, handlers.keySet()
            );
            log.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        return handler;
    }
}