-- ============================================
-- Схема для сервиса warehouse
-- С подробными комментариями для документирования
-- ============================================

-- Создаем схему, если она не существует
CREATE SCHEMA IF NOT EXISTS warehouse_schema;

-- Комментарий к схеме
COMMENT ON SCHEMA warehouse_schema IS 'Схема для микросервиса управления складом (warehouse). Хранит информацию о товарах на складе: количество, габариты, вес, хрупкость.';

-- ============================================
-- Таблица товаров на складе
-- ============================================

-- Создаем таблицу товаров на складе
CREATE TABLE IF NOT EXISTS warehouse_schema.warehouse_product (
    -- Идентификатор товара из сервиса shopping-store
    product_id UUID PRIMARY KEY,

    -- Ширина товара в сантиметрах (минимум 1)
    width DOUBLE PRECISION NOT NULL,

    -- Высота товара в сантиметрах (минимум 1)
    height DOUBLE PRECISION NOT NULL,

    -- Глубина товара в сантиметрах (минимум 1)
    depth DOUBLE PRECISION NOT NULL,

    -- Вес товара в килограммах (минимум 1)
    weight DOUBLE PRECISION NOT NULL,

    -- Признак хрупкости товара (по умолчанию FALSE)
    fragile BOOLEAN NOT NULL DEFAULT FALSE,

    -- Текущее количество товара на складе (минимум 0)
    quantity BIGINT NOT NULL
);

-- ============================================
-- КОММЕНТАРИИ К ТАБЛИЦЕ И КОЛОНКАМ
-- ============================================

COMMENT ON TABLE warehouse_schema.warehouse_product IS 'Товары на складе. Содержит информацию о количестве, габаритах, весе и хрупкости каждого товара.';

COMMENT ON COLUMN warehouse_schema.warehouse_product.product_id IS 'Идентификатор товара из сервиса shopping-store. Является первичным ключом. Соответствует productId в запросах API.';

COMMENT ON COLUMN warehouse_schema.warehouse_product.width IS 'Ширина товара в сантиметрах. Должна быть >= 1. Соответствует полю width в DimensionDto.';

COMMENT ON COLUMN warehouse_schema.warehouse_product.height IS 'Высота товара в сантиметрах. Должна быть >= 1. Соответствует полю height в DimensionDto.';

COMMENT ON COLUMN warehouse_schema.warehouse_product.depth IS 'Глубина товара в сантиметрах. Должна быть >= 1. Соответствует полю depth в DimensionDto.';

COMMENT ON COLUMN warehouse_schema.warehouse_product.weight IS 'Вес товара в килограммах. Должен быть >= 1. Соответствует полю weight в NewProductInWarehouseRequest.';

COMMENT ON COLUMN warehouse_schema.warehouse_product.fragile IS 'Признак хрупкости товара:
- TRUE: товар хрупкий, требует особых условий доставки
- FALSE: товар не хрупкий
Соответствует полю fragile в NewProductInWarehouseRequest.';

COMMENT ON COLUMN warehouse_schema.warehouse_product.quantity IS 'Текущее количество товара на складе. Должно быть >= 0. Увеличивается через API /add, уменьшается при проверке/бронировании.';

-- ============================================
-- Индексы для производительности
-- ============================================

-- Индекс для поиска по количеству (товары, которые заканчиваются)
CREATE INDEX IF NOT EXISTS idx_warehouse_product_quantity ON warehouse_schema.warehouse_product(quantity);

-- Индекс для поиска хрупких товаров
CREATE INDEX IF NOT EXISTS idx_warehouse_product_fragile ON warehouse_schema.warehouse_product(fragile);
