package ru.yandex.practicum.hubrouter.emulator;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.hubrouter.config.SwitchSensorConfig;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class SwitchSensorEventFactory extends BaseSensorEventFactory {
    private final SwitchSensorConfig sensorConfig;

    @Override
    public SensorEventProto createEvent() {
        boolean isOn = ThreadLocalRandom.current().nextBoolean();

        return SensorEventProto.newBuilder()
                .setId(sensorConfig.getId())
                .setTimestamp(createTimestamp())
                .setSwitchSensor(
                        SwitchSensorProto.newBuilder()
                                .setState(isOn)  // правильно: setState
                                .build()
                )
                .build();
    }
}