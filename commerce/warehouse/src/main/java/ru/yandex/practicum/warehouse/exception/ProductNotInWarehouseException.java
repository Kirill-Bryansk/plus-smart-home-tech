package ru.yandex.practicum.warehouse.exception;

public class ProductNotInWarehouseException extends RuntimeException {

    public ProductNotInWarehouseException(String message) {
        super(message);
    }
}
