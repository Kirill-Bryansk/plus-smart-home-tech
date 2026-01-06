package ru.yandex.practicum.collector.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum HubEventType {
    DEVICE_ADDED("DEVICE_ADDED"),
    DEVICE_REMOVED("DEVICE_REMOVED"),
    SCENARIO_ADDED("SCENARIO_ADDED"),
    SCENARIO_REMOVED("SCENARIO_REMOVED");

    private final String value;

    HubEventType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static HubEventType fromValue(String value) {
        for (HubEventType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        log.warn("Неизвестный hub event type: {}", value);
        throw new IllegalArgumentException("Unknown hub event type: " + value);
    }
}
