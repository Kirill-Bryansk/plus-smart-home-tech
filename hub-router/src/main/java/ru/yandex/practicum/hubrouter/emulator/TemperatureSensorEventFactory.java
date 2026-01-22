package ru.yandex.practicum.hubrouter.emulator;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.hubrouter.config.TemperatureSensorConfig;

@RequiredArgsConstructor
public class TemperatureSensorEventFactory extends BaseSensorEventFactory {

    private final TemperatureSensorConfig sensorConfig;

    @Override
    public SensorEventProto createEvent() {
        int temperatureCelsius = getRandomValue(
                sensorConfig.getTemperature().getMinValue(),
                sensorConfig.getTemperature().getMaxValue()
        );
        int temperatureFahrenheit = (int) (temperatureCelsius * 1.8 + 32);

        return SensorEventProto.newBuilder()
                .setId(sensorConfig.getId())
                .setTimestamp(createTimestamp())
                .setTemperatureSensor(
                        TemperatureSensorProto.newBuilder()
                                .setTemperatureC(temperatureCelsius)
                                .setTemperatureF(temperatureFahrenheit)
                                .build())
                .build();
    }
}