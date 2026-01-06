package ru.yandex.practicum.collector.serialiazer;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class GeneralAvroSerializer implements Serializer<SpecificRecordBase> {

    private final EncoderFactory encoderFactory = EncoderFactory.get();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // Ничего не делаем
    }

    @Override
    public byte[] serialize(String topic, SpecificRecordBase data) {
        if (data == null) {
            return null;
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            DatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>(data.getSchema());
            BinaryEncoder encoder = encoderFactory.binaryEncoder(out, null);

            writer.write(data, encoder);
            encoder.flush();

            byte[] result = out.toByteArray();

            // Логируем размер для отладки
            System.out.println("Serialized Avro size: " + result.length + " bytes for " + data.getClass().getSimpleName());

            return result;

        } catch (IOException e) {
            throw new SerializationException("Ошибка сериализации Avro для топика: " + topic, e);
        }
    }

    @Override
    public void close() {
        // Ничего не делаем
    }
}