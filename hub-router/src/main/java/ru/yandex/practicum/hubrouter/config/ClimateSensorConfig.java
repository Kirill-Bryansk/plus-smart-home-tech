package ru.yandex.practicum.hubrouter.config;

import lombok.Data;

@Data
public class ClimateSensorConfig {
    private String id;
    private ValueRange temperature;  // °C
    private ValueRange humidity;     // %
    private ValueRange co2Level;     // ppm
}