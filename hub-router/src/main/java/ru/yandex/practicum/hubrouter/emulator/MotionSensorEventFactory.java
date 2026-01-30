package ru.yandex.practicum.hubrouter.emulator;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.hubrouter.config.MotionSensorConfig;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class MotionSensorEventFactory extends BaseSensorEventFactory {
    private final MotionSensorConfig sensorConfig;

    @Override
    public SensorEventProto createEvent() {
        return SensorEventProto.newBuilder()
                .setId(sensorConfig.getId())
                .setTimestamp(createTimestamp())
                .setMotionSensor(
                        MotionSensorProto.newBuilder()
                                .setLinkQuality(getRandomValue(
                                        sensorConfig.getLinkQuality().getMinValue(),
                                        sensorConfig.getLinkQuality().getMaxValue()
                                ))
                                .setMotion(ThreadLocalRandom.current().nextBoolean())
                                .setVoltage(getRandomValue(
                                        sensorConfig.getVoltage().getMinValue(),
                                        sensorConfig.getVoltage().getMaxValue()
                                ))
                                .build())
                .build();
    }
}