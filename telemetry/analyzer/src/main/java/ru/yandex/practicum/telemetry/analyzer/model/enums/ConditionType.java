package ru.yandex.practicum.telemetry.analyzer.model.enums;

public enum ConditionType {
    MOTION,        // для датчиков движения
    LUMINOSITY,    // для датчиков освещенности
    SWITCH,        // для переключателей
    TEMPERATURE,   // для температурных датчиков
    CO2LEVEL,      // для климатических датчиков
    HUMIDITY       // для климатических датчиков
}