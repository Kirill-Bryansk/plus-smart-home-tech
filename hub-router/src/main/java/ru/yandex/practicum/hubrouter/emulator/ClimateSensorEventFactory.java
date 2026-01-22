package ru.yandex.practicum.hubrouter.emulator;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.hubrouter.config.ClimateSensorConfig;

@RequiredArgsConstructor
public class ClimateSensorEventFactory extends BaseSensorEventFactory {
    private final ClimateSensorConfig sensorConfig;

    @Override
    public SensorEventProto createEvent() {
        int temperatureCelsius = getRandomValue(
                sensorConfig.getTemperature().getMinValue(),
                sensorConfig.getTemperature().getMaxValue()
        );
        int humidity = getRandomValue(
                sensorConfig.getHumidity().getMinValue(),
                sensorConfig.getHumidity().getMaxValue()
        );
        int co2Level = getRandomValue(
                sensorConfig.getCo2Level().getMinValue(),
                sensorConfig.getCo2Level().getMaxValue()
        );

        return SensorEventProto.newBuilder()
                .setId(sensorConfig.getId())
                .setTimestamp(createTimestamp())
                .setClimateSensor(
                        ClimateSensorProto.newBuilder()
                                .setTemperatureC(temperatureCelsius)  // ТОЛЬКО Цельсий
                                .setHumidity(humidity)
                                .setCo2Level(co2Level)
                                .build()
                )
                .build();
    }
}