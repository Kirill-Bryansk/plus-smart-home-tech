package ru.yandex.practicum.shoppingstore.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.yandex.practicum.model.enums.ProductCategory;
import ru.yandex.practicum.model.enums.ProductState;
import ru.yandex.practicum.model.enums.QuantityState;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Товар интернет-магазина.
 */
@Entity
@Table(name = "product")
@Data
public class Product {

    @Id
    @Column(name = "product_id", columnDefinition = "uuid")
    private UUID productId;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "description", nullable = false, columnDefinition = "text")
    private String description;

    @Column(name = "image_src", length = 512)
    private String imageSrc;

    @Column(name = "quantity_state", nullable = false, length = 20)
    private String quantityState;

    @Column(name = "product_state", nullable = false, length = 20)
    private String productState;

    @Column(name = "product_category", nullable = false, length = 20)
    private String productCategory;

    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        if (productId == null) {
            productId = UUID.randomUUID();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
