package ru.yandex.practicum.aggregator.producer;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

/**
 * Сервис для отправки снапшотов в Kafka.
 * Отправляет обновленные снапшоты в топик telemetry.snapshots.v1.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SensorsSnapshotProducer {

    private final Producer<String, SensorsSnapshotAvro> producer;

    /**
     * Отправляет снапшот в Kafka.
     *
     * @param topic топик для отправки
     * @param key ключ сообщения (hubId)
     * @param snapshot снапшот для отправки
     */
    public void send(String topic, String key, SensorsSnapshotAvro snapshot) {
        ProducerRecord<String, SensorsSnapshotAvro> record = new ProducerRecord<>(topic, key, snapshot);

        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception != null) {
                    log.error("Ошибка отправки снапшота для хаба {}: {}", key, exception.getMessage());
                } else {
                    log.debug("Снапшот для хаба {} отправлен в партицию {}, offset {}",
                            key, metadata.partition(), metadata.offset());
                }
            }
        });

        log.trace("Отправлен снапшот для хаба {} в топик {}", key, topic);
    }

    /**
     * Очистка ресурсов при завершении работы приложения.
     * Отправляет все оставшиеся сообщения и закрывает producer.
     */
    @PreDestroy
    void shutdown() {
        log.info("Завершение работы SensorsSnapshotProducer");
        producer.flush();
        producer.close();
        log.debug("Producer закрыт");
    }
}