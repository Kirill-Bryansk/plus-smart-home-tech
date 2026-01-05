package ru.yandex.practicum.collector.dto.sensor;

import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.collector.enums.SensorEventType;

public class SwitchSensorEventDto extends SensorEventDto {

    @NotNull
    private boolean state;

    @Override
    public SensorEventType getType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }

}