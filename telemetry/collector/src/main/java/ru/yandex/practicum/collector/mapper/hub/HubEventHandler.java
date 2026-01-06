package ru.yandex.practicum.collector.mapper.hub;

import ru.yandex.practicum.collector.dto.hub.HubEventDto;

public interface HubEventHandler {
    boolean canHandle(HubEventDto event);
    void handle(HubEventDto event);
}