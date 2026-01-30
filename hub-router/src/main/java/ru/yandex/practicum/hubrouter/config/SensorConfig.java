package ru.yandex.practicum.hubrouter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "sensor")
public class SensorConfig {
    private List<MotionSensorConfig> motionSensors;
    private List<SwitchSensorConfig> switchSensors;
    private List<TemperatureSensorConfig> temperatureSensors;
    private List<LightSensorConfig> lightSensors;
    private List<ClimateSensorConfig> climateSensors;
}