package ru.yandex.practicum.hubrouter.config;

import lombok.Data;

@Data
public class LightSensorConfig {
    private String id;
    private ValueRange luminosity;
    private ValueRange linkQuality;

    // Конструктор с дефолтными значениями
    public LightSensorConfig() {
        this.linkQuality = new ValueRange();
        this.linkQuality.setMinValue(50);  // дефолтные значения
        this.linkQuality.setMaxValue(100);
    }
}