package ru.yandex.practicum.hubrouter.emulator;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventFactory {
    SensorEventProto createEvent();
}