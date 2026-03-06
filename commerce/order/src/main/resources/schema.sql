-- Схема для сервиса заказов
CREATE SCHEMA IF NOT EXISTS order_schema;

-- Таблица заказов
CREATE TABLE IF NOT EXISTS order_schema.orders (
    order_id UUID PRIMARY KEY,
    shopping_cart_id UUID NOT NULL,
    state VARCHAR(50) NOT NULL,
    payment_id UUID,
    delivery_id UUID,
    delivery_weight DOUBLE PRECISION,
    delivery_volume DOUBLE PRECISION,
    fragile BOOLEAN DEFAULT FALSE,
    product_price DOUBLE PRECISION,
    delivery_price DOUBLE PRECISION,
    total_price DOUBLE PRECISION
);

-- Таблица товаров заказа
CREATE TABLE IF NOT EXISTS order_schema.order_items (
    item_id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES order_schema.orders(order_id) ON DELETE CASCADE,
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL
);

-- Индексы для ускорения поиска
CREATE INDEX IF NOT EXISTS idx_orders_state ON order_schema.orders(state);
CREATE INDEX IF NOT EXISTS idx_orders_shopping_cart ON order_schema.orders(shopping_cart_id);
CREATE INDEX IF NOT EXISTS idx_orders_payment ON order_schema.orders(payment_id);
CREATE INDEX IF NOT EXISTS idx_orders_delivery ON order_schema.orders(delivery_id);
CREATE INDEX IF NOT EXISTS idx_order_items_order ON order_schema.order_items(order_id);
