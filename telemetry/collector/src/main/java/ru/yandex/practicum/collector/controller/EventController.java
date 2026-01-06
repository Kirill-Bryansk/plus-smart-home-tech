package ru.yandex.practicum.collector.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.collector.dto.hub.HubEventDto;
import ru.yandex.practicum.collector.dto.sensor.SensorEventDto;
import ru.yandex.practicum.collector.mapper.hub.HubEventHandler;
import ru.yandex.practicum.collector.mapper.sensor.SensorEventHandler;


import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
public class EventController {
	private final List<SensorEventHandler> sensorEventHandlers;
	private final List<HubEventHandler> hubEventHandlers;

	public EventController(
			List<SensorEventHandler> sensorEventHandlers,
			List<HubEventHandler> hubEventHandlers
	) {
		log.info("=== СОЗДАНИЕ EVENT CONTROLLER ===");
		log.info("Найдено обработчиков сенсоров: {}", sensorEventHandlers.size());
		log.info("Найдено обработчиков хаба: {}", hubEventHandlers.size());

		for (SensorEventHandler handler : sensorEventHandlers) {
			log.info("Сенсор обработчик: {}", handler.getClass().getSimpleName());
		}

		for (HubEventHandler handler : hubEventHandlers) {
			log.info("Хаб обработчик: {}", handler.getClass().getSimpleName());
		}

		this.sensorEventHandlers = sensorEventHandlers;
		this.hubEventHandlers = hubEventHandlers;

		log.info("=== EVENT CONTROLLER СОЗДАН ===");
	}

	@PostMapping("/sensors")
	public void collectSensorEvent(@Valid @RequestBody SensorEventDto request) {
		log.info("=== НАЧАЛО ОБРАБОТКИ /events/sensors ===");
		log.info("Получен JSON: {}", request);
		log.info("Тип события: {}", request.getType());
		log.info("ID сенсора: {}", request.getId());
		log.info("ID хаба: {}", request.getHubId());
		log.info("Время: {}", request.getTimestamp());

		// Проверка обработчиков
		log.info("Доступно обработчиков: {}", sensorEventHandlers.size());
		for (SensorEventHandler handler : sensorEventHandlers) {
			log.info("Проверяем обработчик: {}", handler.getClass().getSimpleName());
			log.info("Может обработать? {}", handler.canHandle(request));
		}

		for (SensorEventHandler handler : sensorEventHandlers) {
			if (handler.canHandle(request)) {
				log.info("Найден обработчик: {}", handler.getClass().getSimpleName());
				try {
					handler.handle(request);
					log.info("=== ОБРАБОТКА УСПЕШНО ЗАВЕРШЕНА ===");
					return;
				} catch (Exception e) {
					log.error("Ошибка в обработчике {}: {}", handler.getClass().getSimpleName(), e.getMessage(), e);
					throw new RuntimeException("Ошибка обработки события", e);
				}
			}
		}

		log.error("Не найден обработчик для типа: {}", request.getType());
		throw new IllegalArgumentException("Не могу найти обработчик для события: " + request.getType());
	}

	@PostMapping("/hubs")
	public void collectHubEvent(@Valid @RequestBody HubEventDto request) {
		log.info("=== НАЧАЛО ОБРАБОТКИ /events/hubs ===");
		log.info("Получен POST-запрос /events/hubs с телом: {}", request);

		for (HubEventHandler handler : hubEventHandlers) {
			if (handler.canHandle(request)) {
				log.info("Найден обработчик хаба: {}", handler.getClass().getSimpleName());
				handler.handle(request);
				log.info("=== ОБРАБОТКА ХАБА УСПЕШНО ЗАВЕРШЕНА ===");
				return;
			}
		}

		throw new IllegalArgumentException("Не могу найти обработчик для события: " + request.getType());
	}
}