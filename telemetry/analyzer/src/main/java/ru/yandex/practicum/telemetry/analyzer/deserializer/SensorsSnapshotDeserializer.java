package ru.yandex.practicum.telemetry.analyzer.deserializer;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.io.IOException;
import java.util.Map;

/**
 * Десериализатор для снапшотов состояний датчиков (SensorsSnapshotAvro).
 * Работает без Schema Registry, используя встроенную схему Avro.
 */
public class SensorsSnapshotDeserializer implements Deserializer<SensorsSnapshotAvro> {

    private static final Logger log = LoggerFactory.getLogger(SensorsSnapshotDeserializer.class);
    private static final Schema SCHEMA = SensorsSnapshotAvro.getClassSchema();
    private static final int MAX_DATA_SIZE = 10 * 1024 * 1024; // 10 MB

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // Десериализатор не требует дополнительной конфигурации
        log.trace("Конфигурация SensorsSnapshotDeserializer: isKey={}", isKey);
    }

    @Override
    public SensorsSnapshotAvro deserialize(String topic, byte[] data) {
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
            SpecificDatumReader<SensorsSnapshotAvro> reader = new SpecificDatumReader<>(SCHEMA);
            SensorsSnapshotAvro snapshot = reader.read(null, decoder);

            log.trace("Успешно десериализован SensorsSnapshotAvro для топика {}: hubId={}, кол-во датчиков={}",
                    topic, snapshot.getHubId(), snapshot.getSensorsState().size());
            return snapshot;
        } catch (IOException e) {
            log.error("Ошибка десериализации SensorsSnapshotAvro для топика {} (длина данных: {} байт)",
                    topic, data.length, e);
            throw new SerializationException("Ошибка десериализации SensorsSnapshotAvro", e);
        }
    }

    @Override
    public void close() {
        // Нет ресурсов для освобождения
        log.trace("Закрытие SensorsSnapshotDeserializer");
    }
}