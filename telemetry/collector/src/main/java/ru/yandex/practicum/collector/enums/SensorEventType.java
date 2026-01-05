package ru.yandex.practicum.collector.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum SensorEventType {
    MOTION_SENSOR_EVENT("MOTION_SENSOR_EVENT"),
    TEMPERATURE_SENSOR_EVENT("TEMPERATURE_SENSOR_EVENT"),
    LIGHT_SENSOR_EVENT("LIGHT_SENSOR_EVENT"),
    CLIMATE_SENSOR_EVENT("CLIMATE_SENSOR_EVENT"),
    SWITCH_SENSOR_EVENT("SWITCH_SENSOR_EVENT"),
    UNKNOWN("UNKNOWN"); // для неизвестных типов

    private final String value;

    SensorEventType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static SensorEventType fromValue(String value) {
        for (SensorEventType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        log.warn("Неизвестный sensor event type: {}", value);
        return UNKNOWN; // вместо исключения
    }
}
