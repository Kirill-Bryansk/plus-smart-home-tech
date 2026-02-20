-- ============================================
-- Упрощённая схема для H2 (разработка)
-- Сервис: shopping-store
-- ============================================

-- Таблица товаров
CREATE TABLE product (
    product_id UUID PRIMARY KEY DEFAULT RANDOM_UUID(),
    product_name VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    image_src VARCHAR(512),
    quantity_state VARCHAR(20) NOT NULL,
    product_state VARCHAR(20) NOT NULL,
    product_category VARCHAR(20) NOT NULL,
    price NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Индексы
CREATE INDEX IF NOT EXISTS idx_product_category ON product(product_category);
CREATE INDEX IF NOT EXISTS idx_product_state ON product(product_state);
CREATE INDEX IF NOT EXISTS idx_quantity_state ON product(quantity_state);
