package ru.yandex.practicum.shoppingcart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Главный класс микросервиса Shopping Cart.
 * Управляет корзинами пользователей.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ShoppingCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingCartApplication.class, args);
    }
}
