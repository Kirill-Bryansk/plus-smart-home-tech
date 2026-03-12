-- Схема для сервиса оплаты
CREATE SCHEMA IF NOT EXISTS payment_schema;

-- Таблица оплат
CREATE TABLE IF NOT EXISTS payment_schema.payments (
    payment_id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    product_cost DOUBLE PRECISION,
    delivery_cost DOUBLE PRECISION,
    total_cost DOUBLE PRECISION,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING'
);

-- Индекс для поиска по заказу
CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payment_schema.payments(order_id);

-- Индекс для поиска по статусу
CREATE INDEX IF NOT EXISTS idx_payments_status ON payment_schema.payments(status);
