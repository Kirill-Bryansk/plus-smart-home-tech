package ru.yandex.practicum.collector.mapper.sensor;


import ru.yandex.practicum.collector.dto.sensor.SensorEventDto;

public interface SensorEventHandler {
	boolean canHandle(SensorEventDto event);  // вместо getMessageType()

	void handle(SensorEventDto event);
}