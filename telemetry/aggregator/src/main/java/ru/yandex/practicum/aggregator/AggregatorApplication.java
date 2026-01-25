package ru.yandex.practicum.aggregator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.aggregator.consumer.SensorEventConsumerRunner;

/**
 * Главный класс приложения Aggregator.
 * Запускает Spring Boot приложение и основной обработчик событий.
 */
@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
public class AggregatorApplication {

    public static void main(String[] args) {
        log.info("Запуск приложения Aggregator");

        // Запускаем Spring Boot приложение
        ConfigurableApplicationContext context = SpringApplication.run(AggregatorApplication.class, args);

        log.debug("Spring контекст успешно создан");

        // Получаем и запускаем основной обработчик событий
        SensorEventConsumerRunner runner = context.getBean(SensorEventConsumerRunner.class);
        runner.start();

        log.info("Приложение Aggregator завершило работу");
    }
}