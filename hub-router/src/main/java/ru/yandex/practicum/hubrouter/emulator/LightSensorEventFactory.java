package ru.yandex.practicum.hubrouter.emulator;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.hubrouter.config.LightSensorConfig;

@RequiredArgsConstructor
public class LightSensorEventFactory extends BaseSensorEventFactory {
    private final LightSensorConfig sensorConfig;

    @Override
    public SensorEventProto createEvent() {
        int luminosity = getRandomValue(
                sensorConfig.getLuminosity().getMinValue(),
                sensorConfig.getLuminosity().getMaxValue()
        );
        int linkQuality = getRandomValue(
                sensorConfig.getLinkQuality().getMinValue(),  // Используем дефолтные 50-100
                sensorConfig.getLinkQuality().getMaxValue()
        );

        return SensorEventProto.newBuilder()
                .setId(sensorConfig.getId())
                .setTimestamp(createTimestamp())
                .setLightSensor(
                        LightSensorProto.newBuilder()
                                .setLuminosity(luminosity)
                                .setLinkQuality(linkQuality)  // Добавляем
                                .build()
                )
                .build();
    }
}