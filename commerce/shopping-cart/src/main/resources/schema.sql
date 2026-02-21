-- ============================================
-- Схема для сервиса shopping-cart
-- С подробными комментариями для документирования
-- ============================================

-- Создаем схему, если она не существует
CREATE SCHEMA IF NOT EXISTS shopping_cart_schema;

-- Комментарий к схеме
COMMENT ON SCHEMA shopping_cart_schema IS 'Схема для микросервиса управления корзиной (shopping-cart). Хранит корзины пользователей и товары в них.';

-- ============================================
-- Таблица корзин пользователей
-- ============================================

-- Создаем таблицу корзин
CREATE TABLE IF NOT EXISTS shopping_cart_schema.shopping_carts (
    -- Уникальный идентификатор корзины
    shopping_cart_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Имя пользователя - владельца корзины
    username VARCHAR(32) UNIQUE NOT NULL,

    -- Состояние корзины (ACTIVE, INACTIVE, DELETED)
    cart_state VARCHAR(20) DEFAULT 'ACTIVE' CHECK (cart_state IN ('ACTIVE', 'INACTIVE', 'DELETED'))
);

-- ============================================
-- КОММЕНТАРИИ К ТАБЛИЦЕ И КОЛОНКАМ
-- ============================================

COMMENT ON TABLE shopping_cart_schema.shopping_carts IS 'Корзины пользователей. Каждый пользователь может иметь только одну активную корзину.';

COMMENT ON COLUMN shopping_cart_schema.shopping_carts.shopping_cart_id IS 'Уникальный идентификатор корзины в формате UUID. Генерируется автоматически. Пример: 550e8400-e29b-41d4-a716-446655440000';

COMMENT ON COLUMN shopping_cart_schema.shopping_carts.username IS 'Имя пользователя - владельца корзины. Должно быть уникальным. Соответствует параметру username в API. Максимальная длина - 32 символа.';

COMMENT ON COLUMN shopping_cart_schema.shopping_carts.cart_state IS 'Состояние корзины: ACTIVE, INACTIVE, DELETED';

-- ============================================
-- Таблица товаров в корзине
-- ============================================

-- Создаем таблицу товаров
CREATE TABLE IF NOT EXISTS shopping_cart_schema.cart_products (
    -- Внешний ключ на таблицу shopping_carts
    shopping_cart_id UUID NOT NULL,

    -- Идентификатор товара из сервиса shopping-store
    product_id UUID NOT NULL,

    -- Количество единиц товара в корзине (должно быть > 0)
    quantity INTEGER NOT NULL,

    -- Первичный ключ (составной)
    PRIMARY KEY (shopping_cart_id, product_id),

    -- Внешний ключ с каскадным удалением
    FOREIGN KEY (shopping_cart_id) REFERENCES shopping_cart_schema.shopping_carts(shopping_cart_id) ON DELETE CASCADE
);

-- ============================================
-- КОММЕНТАРИИ К ТАБЛИЦЕ И КОЛОНКАМ
-- ============================================

COMMENT ON TABLE shopping_cart_schema.cart_products IS 'Товары в корзинах пользователей. Содержит информацию о том, какие товары и в каком количестве добавлены в каждую корзину.';

COMMENT ON COLUMN shopping_cart_schema.cart_products.shopping_cart_id IS 'Внешний ключ на таблицу shopping_carts. Указывает, к какой корзине относится товар. При удалении корзины все связанные товары удаляются автоматически (CASCADE).';

COMMENT ON COLUMN shopping_cart_schema.cart_products.product_id IS 'Идентификатор товара из сервиса shopping-store. Используется для получения информации о товаре через API магазина.';

COMMENT ON COLUMN shopping_cart_schema.cart_products.quantity IS 'Количество единиц товара в корзине. Должно быть положительным числом (больше 0).';

-- ============================================
-- Индексы для производительности
-- ============================================

-- Индекс для быстрого поиска корзины по пользователю
CREATE INDEX IF NOT EXISTS idx_shopping_carts_username ON shopping_cart_schema.shopping_carts(username);

-- Индекс для поиска товаров в корзине
CREATE INDEX IF NOT EXISTS idx_cart_products_cart ON shopping_cart_schema.cart_products(shopping_cart_id);

-- Индекс для поиска конкретного товара в корзине (покрывающий)
CREATE INDEX IF NOT EXISTS idx_cart_products_cart_product ON shopping_cart_schema.cart_products(shopping_cart_id, product_id);
