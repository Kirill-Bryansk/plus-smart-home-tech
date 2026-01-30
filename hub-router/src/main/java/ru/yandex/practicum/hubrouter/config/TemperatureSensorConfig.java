package ru.yandex.practicum.hubrouter.config;

import lombok.Data;

@Data
public class TemperatureSensorConfig {
    private String id;
    private ValueRange temperature;
}