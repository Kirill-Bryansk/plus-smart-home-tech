package ru.yandex.practicum.hubrouter.config;

import lombok.Data;

@Data
public class SwitchSensorConfig {
    private String id;
    // нет диапазонов, только состояние вкл/выкл
}
