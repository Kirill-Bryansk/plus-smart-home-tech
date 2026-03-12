-- Схема для сервиса доставки
CREATE SCHEMA IF NOT EXISTS delivery_schema;

-- Таблица доставок
CREATE TABLE IF NOT EXISTS delivery_schema.deliveries (
    delivery_id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    volume DOUBLE PRECISION,
    weight DOUBLE PRECISION,
    fragile BOOLEAN DEFAULT FALSE,

    -- Адрес склада (откуда)
    from_country VARCHAR(255),
    from_city VARCHAR(255),
    from_street VARCHAR(255),
    from_house VARCHAR(255),
    from_flat VARCHAR(255),

    -- Адрес доставки (куда)
    to_country VARCHAR(255),
    to_city VARCHAR(255),
    to_street VARCHAR(255),
    to_house VARCHAR(255),
    to_flat VARCHAR(255),

    -- Статус и стоимость
    status VARCHAR(50) NOT NULL DEFAULT 'CREATED',
    cost DOUBLE PRECISION
);

-- Индекс для поиска по заказу
CREATE INDEX IF NOT EXISTS idx_deliveries_order_id ON delivery_schema.deliveries(order_id);
