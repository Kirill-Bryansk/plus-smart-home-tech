-- ============================================
-- Схема для сервиса warehouse
-- Только таблица товаров на складе
-- ============================================

-- Создаем схему, если она не существует
CREATE SCHEMA IF NOT EXISTS warehouse_schema;

-- Комментарий к схеме
COMMENT ON SCHEMA warehouse_schema IS 'Схема для микросервиса управления складом (warehouse). Хранит информацию о товарах на складе: количество, габариты, вес, хрупкость.';

-- Устанавливаем поиск по схеме по умолчанию
SET search_path TO warehouse_schema;

-- ============================================
-- Таблица товаров на складе
-- ============================================

-- Удаляем таблицу, если она существует (для чистой инициализации)
DROP TABLE IF EXISTS warehouse_schema.warehouse_product CASCADE;

-- Создаем таблицу товаров на складе
CREATE TABLE IF NOT EXISTS warehouse_product (
    product_id UUID PRIMARY KEY,
    width DOUBLE PRECISION NOT NULL CHECK (width >= 1),
    height DOUBLE PRECISION NOT NULL CHECK (height >= 1),
    depth DOUBLE PRECISION NOT NULL CHECK (depth >= 1),
    weight DOUBLE PRECISION NOT NULL CHECK (weight >= 1),
    fragile BOOLEAN NOT NULL DEFAULT FALSE,
    quantity BIGINT NOT NULL CHECK (quantity >= 0)
);

-- Комментарии к таблице warehouse_product
COMMENT ON TABLE warehouse_product IS 'Товары на складе. Содержит информацию о количестве, габаритах, весе и хрупкости каждого товара.';

COMMENT ON COLUMN warehouse_product.product_id IS 'Идентификатор товара из сервиса shopping-store. Является первичным ключом. Соответствует productId в запросах API.';

COMMENT ON COLUMN warehouse_product.width IS 'Ширина товара в сантиметрах. Должна быть >= 1. Соответствует полю width в DimensionDto.';

COMMENT ON COLUMN warehouse_product.height IS 'Высота товара в сантиметрах. Должна быть >= 1. Соответствует полю height в DimensionDto.';

COMMENT ON COLUMN warehouse_product.depth IS 'Глубина товара в сантиметрах. Должна быть >= 1. Соответствует полю depth в DimensionDto.';

COMMENT ON COLUMN warehouse_product.weight IS 'Вес товара в килограммах. Должен быть >= 1. Соответствует полю weight в NewProductInWarehouseRequest.';

COMMENT ON COLUMN warehouse_product.fragile IS 'Признак хрупкости товара:
- TRUE: товар хрупкий, требует особых условий доставки
- FALSE: товар не хрупкий
Соответствует полю fragile в NewProductInWarehouseRequest.';

COMMENT ON COLUMN warehouse_product.quantity IS 'Текущее количество товара на складе. Должно быть >= 0. Увеличивается через API /add, уменьшается при проверке/бронировании.';

COMMENT ON CONSTRAINT warehouse_product_width_check ON warehouse_product IS 'Проверяет, что ширина товара положительная (>= 1).';
COMMENT ON CONSTRAINT warehouse_product_height_check ON warehouse_product IS 'Проверяет, что высота товара положительная (>= 1).';
COMMENT ON CONSTRAINT warehouse_product_depth_check ON warehouse_product IS 'Проверяет, что глубина товара положительная (>= 1).';
COMMENT ON CONSTRAINT warehouse_product_weight_check ON warehouse_product IS 'Проверяет, что вес товара положительный (>= 1).';
COMMENT ON CONSTRAINT warehouse_product_quantity_check ON warehouse_product IS 'Проверяет, что количество товара неотрицательное (>= 0). Отрицательное количество недопустимо.';

-- ============================================
-- Индексы для производительности
-- ============================================

-- Индекс для поиска по количеству (товары, которые заканчиваются)
CREATE INDEX idx_warehouse_product_quantity ON warehouse_product(quantity) WHERE quantity < 10;
COMMENT ON INDEX idx_warehouse_product_quantity IS 'Индекс для быстрого поиска товаров, которые заканчиваются (количество меньше 10).';

-- Индекс для поиска хрупких товаров
CREATE INDEX idx_warehouse_product_fragile ON warehouse_product(fragile) WHERE fragile = TRUE;
COMMENT ON INDEX idx_warehouse_product_fragile IS 'Индекс для быстрого поиска всех хрупких товаров на складе.';

-- ============================================
-- Права доступа
-- ============================================

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA warehouse_schema TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA warehouse_schema TO postgres;