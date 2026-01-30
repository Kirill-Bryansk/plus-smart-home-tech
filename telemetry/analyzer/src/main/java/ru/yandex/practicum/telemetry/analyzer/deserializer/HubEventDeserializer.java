package ru.yandex.practicum.telemetry.analyzer.deserializer;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.io.IOException;
import java.util.Map;

/**
 * Десериализатор для событий хаба (HubEventAvro).
 * Работает без Schema Registry, используя встроенную схему Avro.
 */
public class HubEventDeserializer implements Deserializer<HubEventAvro> {

    private static final Logger log = LoggerFactory.getLogger(HubEventDeserializer.class);
    private static final Schema SCHEMA = HubEventAvro.getClassSchema();
    private static final int MAX_DATA_SIZE = 10 * 1024 * 1024; // 10 MB

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // Десериализатор не требует дополнительной конфигурации
        log.trace("Конфигурация HubEventDeserializer: isKey={}", isKey);
    }

    @Override
    public HubEventAvro deserialize(String topic, byte[] data) {
        if (data == null) {
            log.debug("Получено null сообщение для топика {}", topic);
            return null;
        }

        if (data.length == 0) {
            log.debug("Получено пустое сообщение (0 байт) для топика {}", topic);
            return null;
        }

        // Проверка максимального размера данных
        if (data.length > MAX_DATA_SIZE) {
            log.error("Сообщение слишком большое для топика {}: {} байт (максимум: {})",
                    topic, data.length, MAX_DATA_SIZE);
            throw new SerializationException(
                    String.format("Сообщение превышает максимальный размер: %d байт > %d байт",
                            data.length, MAX_DATA_SIZE));
        }

        try {
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            SpecificDatumReader<HubEventAvro> reader = new SpecificDatumReader<>(SCHEMA);
            HubEventAvro event = reader.read(null, decoder);

            log.trace("Успешно десериализовано HubEventAvro для топика {}: hubId={}",
                    topic, event.getHubId());
            return event;
        } catch (IOException e) {
            log.error("Ошибка десериализации HubEventAvro для топика {} (длина данных: {} байт)",
                    topic, data.length, e);
            throw new SerializationException("Ошибка десериализации HubEventAvro", e);
        }
    }

    @Override
    public void close() {
        // Нет ресурсов для освобождения
        log.trace("Закрытие HubEventDeserializer");
    }
}