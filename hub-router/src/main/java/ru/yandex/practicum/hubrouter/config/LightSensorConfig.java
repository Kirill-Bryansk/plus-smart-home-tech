package ru.yandex.practicum.hubrouter.config;

import lombok.Data;

@Data
public class LightSensorConfig {
    private String id;
    private ValueRange luminosity;  // освещённость в люксах
}