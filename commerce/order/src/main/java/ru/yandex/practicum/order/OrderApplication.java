package ru.yandex.practicum.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.api.WarehouseApi;

/**
 * Приложение сервиса управления заказами.
 */
@SpringBootApplication
@EnableFeignClients(basePackageClasses = WarehouseApi.class)
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
