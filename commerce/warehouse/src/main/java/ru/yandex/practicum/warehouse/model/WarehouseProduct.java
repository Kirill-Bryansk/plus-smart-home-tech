package ru.yandex.practicum.warehouse.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Товар на складе.
 * Сущность совместима с H2 (dev) и PostgreSQL (prod).
 */
@Entity
@Table(name = "warehouse_product")
@Data
public class WarehouseProduct {

    @Id
    @Column(name = "product_id", columnDefinition = "uuid")
    private UUID productId;

    @Column(name = "width", nullable = false)
    private Double width;

    @Column(name = "height", nullable = false)
    private Double height;

    @Column(name = "depth", nullable = false)
    private Double depth;

    @Column(name = "weight", nullable = false)
    private Double weight;

    @Column(name = "fragile", nullable = false, columnDefinition = "boolean default false")
    private Boolean fragile;

    @Column(name = "quantity", nullable = false)
    private Long quantity;
}
