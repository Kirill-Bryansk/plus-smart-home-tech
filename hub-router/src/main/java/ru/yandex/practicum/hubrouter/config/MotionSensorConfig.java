package ru.yandex.practicum.hubrouter.config;

import lombok.Data;

@Data
public class MotionSensorConfig {
    private String id;
    private ValueRange linkQuality;
    private ValueRange voltage;
}