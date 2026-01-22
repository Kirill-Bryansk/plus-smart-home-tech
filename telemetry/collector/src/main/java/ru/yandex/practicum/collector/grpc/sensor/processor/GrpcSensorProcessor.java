package ru.yandex.practicum.collector.grpc.sensor.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.dto.sensor.SensorEventDto;
import ru.yandex.practicum.collector.grpc.sensor.mapper.GrpcToDtoMapper;
import ru.yandex.practicum.collector.mapper.sensor.SensorEventHandler;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.util.List;

/**
 * Сервис для обработки gRPC событий от датчиков.
 * Отвечает за бизнес-логику: маппинг Protobuf → DTO и поиск обработчика.
 * Не зависит от gRPC транспорта.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GrpcSensorProcessor {

    private final GrpcToDtoMapper grpcToDtoMapper;
    private final List<SensorEventHandler> handlers;

    /**
     * Обрабатывает событие от датчика: преобразует Protobuf в DTO и отправляет в обработчик
     * @param proto Событие в формате Protobuf
     * @throws IllegalArgumentException если не найден обработчик для типа события
     * @throws RuntimeException если произошла ошибка при маппинге или обработке
     */
    public void processSensorEvent(SensorEventProto proto) {
        log.info("НАЧАЛО обработки события датчика в GrpcSensorProcessor");
        log.info("ID события: {}, Hub ID: {}", proto.getId(), proto.getHubId());

        try {
            // Шаг 1: Преобразование Protobuf → DTO
            log.debug("Вызываю GrpcToDtoMapper для преобразования Protobuf → DTO");
            SensorEventDto dto = grpcToDtoMapper.fromProto(proto);
            log.info("DTO создан успешно. Тип: {}, ID: {}", dto.getType(), dto.getId());

            // Шаг 2: Поиск подходящего обработчика
            log.debug("Ищу подходящий SensorEventHandler для типа: {}", dto.getType());
            SensorEventHandler handler = handlers.stream()
                    .filter(h -> h.canHandle(dto))
                    .findFirst()
                    .orElseThrow(() -> {
                        log.error("ОШИБКА: Не найден обработчик для типа события: {}", dto.getType());
                        return new IllegalArgumentException(
                                String.format("Не найден обработчик для типа события: %s", dto.getType())
                        );
                    });

            log.info("Найден обработчик: {}", handler.getClass().getSimpleName());

            // Шаг 3: Делегирование обработки конкретному обработчику
            log.debug("Делегирую обработку событию в handler.handle()");
            handler.handle(dto);
            log.info("Обработка события успешно завершена");

        } catch (IllegalArgumentException e) {
            log.error("ОШИБКА ВАЛИДАЦИИ: {}", e.getMessage());
            throw e; // Пробрасываем дальше для обработки в gRPC слое
        } catch (Exception e) {
            log.error("НЕОЖИДАННАЯ ОШИБКА при обработке события: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка обработки события: " + e.getMessage(), e);
        } finally {
            log.info("ЗАВЕРШЕНИЕ обработки события в GrpcSensorProcessor");
        }
    }
}
