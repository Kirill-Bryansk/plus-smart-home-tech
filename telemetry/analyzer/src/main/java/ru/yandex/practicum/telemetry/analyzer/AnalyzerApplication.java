package ru.yandex.practicum.telemetry.analyzer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.telemetry.analyzer.consumer.hub.HubEventProcessor;
import ru.yandex.practicum.telemetry.analyzer.consumer.snapshot.SnapshotProcessor;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
public class AnalyzerApplication {

    public static void main(String[] args) {
        log.info("🚀 Запуск Analyzer Application...");
        ConfigurableApplicationContext context = SpringApplication.run(AnalyzerApplication.class, args);

        // Получаем бины процессоров
        HubEventProcessor hubEventProcessor = context.getBean(HubEventProcessor.class);
        SnapshotProcessor snapshotProcessor = context.getBean(SnapshotProcessor.class);

        // Запускаем HubEventProcessor в отдельном потоке
        Thread hubThread = new Thread(hubEventProcessor);
        hubThread.setName("HubEventProcessor-Thread");
        hubThread.start();
        log.info("✅ HubEventProcessor запущен в отдельном потоке");

        // Запускаем SnapshotProcessor в отдельном потоке
        Thread snapshotThread = new Thread(snapshotProcessor);
        snapshotThread.setName("SnapshotProcessor-Thread");
        snapshotThread.start();
        log.info("✅ SnapshotProcessor запущен в отдельном потоке");

        // Регистрируем graceful shutdown
        registerShutdownHook(context, hubEventProcessor, snapshotProcessor);
    }

    private static void registerShutdownHook(
            ConfigurableApplicationContext context,
            HubEventProcessor hubEventProcessor,
            SnapshotProcessor snapshotProcessor) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("🛑 Получен сигнал завершения работы...");

            // Останавливаем процессоры
            hubEventProcessor.stop();
            snapshotProcessor.stop();

            // Ждем завершения потоков
            try {
                Thread.sleep(2000); // Даем время на graceful shutdown
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Закрываем контекст Spring
            context.close();

            log.info("👋 Analyzer Application завершен");
        }));
    }
}