package ru.yandex.practicum.aggregator.serializer;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Универсальный сериализатор для любых Avro объектов.
 * Сериализует Avro объекты в байтовый массив для отправки в Kafka.
 */
@Slf4j
public class GeneralAvroSerializer implements Serializer<SpecificRecordBase> {

    private final EncoderFactory encoderFactory = EncoderFactory.get();

    @Override
    public byte[] serialize(String topic, SpecificRecordBase data) {
        if (data == null) {
            log.debug("Нет данных для сериализации в топик {}", topic);
            return null;
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Создаем кодировщик и записываем данные
            BinaryEncoder encoder = encoderFactory.binaryEncoder(outputStream, null);
            DatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>(data.getSchema());

            writer.write(data, encoder);
            encoder.flush();

            log.trace("Успешно сериализовано сообщение для топика {}", topic);
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Ошибка сериализации Avro данных для топика {}", topic, e);
            throw new SerializationException("Не удалось сериализовать Avro сообщение", e);
        }
    }
}