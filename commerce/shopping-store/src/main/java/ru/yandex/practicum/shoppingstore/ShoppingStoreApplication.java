package ru.yandex.practicum.shoppingstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Главный класс микросервиса Shopping Store.
 * Управляет товарами интернет-магазина.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ShoppingStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingStoreApplication.class, args);
    }
}
