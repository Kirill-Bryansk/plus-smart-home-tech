package ru.yandex.practicum.collector.dto.sensor;

import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.collector.enums.SensorEventType;

public class MotionSensorEventDto extends SensorEventDto {

    @NotNull
    private int linkQuality;

    @NotNull
    private boolean motion;

    @NotNull
    private int voltage;

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }

}
