-- ============================================
-- Упрощённая схема для H2 (разработка)
-- Сервис: warehouse
-- ============================================

-- Таблица товаров на складе
CREATE TABLE IF NOT EXISTS warehouse_product (
    product_id UUID PRIMARY KEY,
    width DOUBLE NOT NULL CHECK (width >= 1),
    height DOUBLE NOT NULL CHECK (height >= 1),
    depth DOUBLE NOT NULL CHECK (depth >= 1),
    weight DOUBLE NOT NULL CHECK (weight >= 1),
    fragile BOOLEAN NOT NULL DEFAULT FALSE,
    quantity BIGINT NOT NULL CHECK (quantity >= 0)
);

-- Индексы
CREATE INDEX IF NOT EXISTS idx_warehouse_quantity ON warehouse_product(quantity);
CREATE INDEX IF NOT EXISTS idx_warehouse_fragile ON warehouse_product(fragile);
