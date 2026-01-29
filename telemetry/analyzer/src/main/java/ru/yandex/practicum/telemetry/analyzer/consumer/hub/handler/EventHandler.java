package ru.yandex.practicum.telemetry.analyzer.consumer.hub.handler;

/**
 * Интерфейс обработчика событий хаба.
 * Каждый обработчик отвечает за конкретный тип события.
 */
public interface EventHandler {

    /**
     * Проверяет, может ли обработчик обработать данный тип события
     * @param payload событие из Kafka
     * @return true если обработчик может обработать событие
     */
    boolean canHandle(Object payload);

    /**
     * Обрабатывает событие
     * @param hubId идентификатор хаба
     * @param payload событие для обработки
     */
    void handle(String hubId, Object payload);
}