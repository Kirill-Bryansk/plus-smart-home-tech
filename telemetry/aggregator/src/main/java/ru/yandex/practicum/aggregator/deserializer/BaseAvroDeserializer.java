package ru.yandex.practicum.aggregator.deserializer;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

/**
 * Базовый десериализатор для Avro объектов.
 * Используется для создания конкретных десериализаторов для каждого типа Avro.
 */
@Slf4j
public class BaseAvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {

    private final DecoderFactory decoderFactory;
    private final DatumReader<T> reader;

    /**
     * Конструктор с указанием схемы Avro.
     * @param schema схема Avro для десериализации
     */
    public BaseAvroDeserializer(Schema schema) {
        this(DecoderFactory.get(), schema);
    }

    /**
     * Конструктор с возможностью указать фабрику декодеров.
     * @param decoderFactory фабрика для создания декодеров
     * @param schema схема Avro для десериализации
     */
    public BaseAvroDeserializer(DecoderFactory decoderFactory, Schema schema) {
        this.decoderFactory = decoderFactory;
        this.reader = new SpecificDatumReader<>(schema);
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            if (data == null) {
                log.debug("Получены пустые данные из топика {}", topic);
                return null;
            }

            BinaryDecoder decoder = decoderFactory.binaryDecoder(data, null);
            return reader.read(null, decoder);

        } catch (Exception e) {
            log.error("Ошибка при десериализации данных из топика {}", topic, e);
            throw new SerializationException("Не удалось десериализовать Avro данные", e);
        }
    }
}