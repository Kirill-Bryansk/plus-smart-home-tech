-- ============================================
-- Упрощённая схема для H2 (разработка)
-- Сервис: shopping-cart
-- ============================================

-- Таблица корзин
CREATE TABLE IF NOT EXISTS shopping_carts (
    shopping_cart_id UUID PRIMARY KEY DEFAULT RANDOM_UUID(),
    username VARCHAR(32) UNIQUE NOT NULL,
    cart_state VARCHAR(10) DEFAULT 'ACTIVE'
);

-- Таблица товаров в корзине
CREATE TABLE IF NOT EXISTS cart_products (
    shopping_cart_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (shopping_cart_id, product_id),
    FOREIGN KEY (shopping_cart_id) REFERENCES shopping_carts(shopping_cart_id) ON DELETE CASCADE
);

-- Индексы
CREATE INDEX IF NOT EXISTS idx_shopping_carts_username ON shopping_carts(username);
CREATE INDEX IF NOT EXISTS idx_cart_products_cart ON cart_products(shopping_cart_id);
