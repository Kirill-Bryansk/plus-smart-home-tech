package ru.yandex.practicum.hubrouter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
public class HubRouterApplication {
    public static void main(String[] args) {
        SpringApplication.run(HubRouterApplication.class, args);
    }
}