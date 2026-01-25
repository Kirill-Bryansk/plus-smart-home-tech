package ru.yandex.practicum.aggregator.deserializer;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

/**
 * Десериализатор для событий от датчиков (SensorEventAvro).
 * Наследуется от базового десериализатора.
 */
public class SensorEventDeserializer extends BaseAvroDeserializer<SensorEventAvro> {

    /**
     * Конструктор. Передает схему SensorEventAvro в базовый класс.
     */
    public SensorEventDeserializer() {
        super(SensorEventAvro.getClassSchema());
    }
}