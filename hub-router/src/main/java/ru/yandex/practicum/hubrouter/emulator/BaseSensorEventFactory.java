package ru.yandex.practicum.hubrouter.emulator;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

public abstract class BaseSensorEventFactory implements SensorEventFactory {

    protected int getRandomValue(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    protected com.google.protobuf.Timestamp createTimestamp() {
        Instant now = Instant.now();
        return com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(now.getEpochSecond())
                .setNanos(now.getNano())
                .build();
    }
}