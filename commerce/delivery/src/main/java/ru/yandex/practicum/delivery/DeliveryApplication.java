package ru.yandex.practicum.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Микросервис доставки.
 * Отвечает за расчёт стоимости доставки и управление статусами доставки заказов.
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "ru.yandex.practicum.api")
public class DeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryApplication.class, args);
    }
}
