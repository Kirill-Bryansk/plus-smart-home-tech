-- ============================================
-- Схема для сервиса shopping-cart
-- ============================================

-- Создаем схему, если она не существует
CREATE SCHEMA IF NOT EXISTS shopping_cart_schema;

-- Комментарий к схеме
COMMENT ON SCHEMA shopping_cart_schema IS 'Схема для микросервиса управления корзиной (shopping-cart). Хранит корзины пользователей и товары в них.';

-- Устанавливаем поиск по схеме по умолчанию
SET search_path TO shopping_cart_schema;

-- ============================================
-- Таблица корзин пользователей
-- ============================================

-- Удаляем таблицы, если они существуют (для чистой инициализации)
DROP TABLE IF EXISTS shopping_cart_schema.cart_products CASCADE;
DROP TABLE IF EXISTS shopping_cart_schema.shopping_carts CASCADE;

-- Создаем таблицу корзин
CREATE TABLE IF NOT EXISTS shopping_carts (
    shopping_cart_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(32) UNIQUE NOT NULL,
    cart_state VARCHAR(10) DEFAULT 'ACTIVE'
);

-- Комментарии к таблице shopping_carts
COMMENT ON TABLE shopping_carts IS 'Корзины пользователей. Каждый пользователь может иметь только одну активную корзину.';

COMMENT ON COLUMN shopping_carts.shopping_cart_id IS 'Уникальный идентификатор корзины в формате UUID. Генерируется автоматически. Пример: 550e8400-e29b-41d4-a716-446655440000';

COMMENT ON COLUMN shopping_carts.username IS 'Имя пользователя - владельца корзины. Должно быть уникальным. Соответствует параметру username в API. Максимальная длина - 32 символа.';

COMMENT ON COLUMN shopping_carts.cart_state IS 'Состояние корзины:
- ACTIVE: активна, можно добавлять товары
- INACTIVE: неактивна (например, после оформления заказа)
- DELETED: удалена';

-- ============================================
-- Таблица товаров в корзине
-- ============================================

-- Создаем таблицу товаров
CREATE TABLE IF NOT EXISTS cart_products (
    shopping_cart_id UUID REFERENCES shopping_carts (shopping_cart_id) ON DELETE CASCADE,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (shopping_cart_id, product_id)
);

-- Комментарии к таблице cart_products
COMMENT ON TABLE cart_products IS 'Товары в корзинах пользователей. Содержит информацию о том, какие товары и в каком количестве добавлены в каждую корзину.';

COMMENT ON COLUMN cart_products.shopping_cart_id IS 'Внешний ключ на таблицу shopping_carts. Указывает, к какой корзине относится товар. При удалении корзины все связанные товары удаляются автоматически (CASCADE).';

COMMENT ON COLUMN cart_products.product_id IS 'Идентификатор товара из сервиса shopping-store. Используется для получения информации о товаре через API магазина.';

COMMENT ON COLUMN cart_products.quantity IS 'Количество единиц товара в корзине. Должно быть положительным числом (больше 0).';

COMMENT ON CONSTRAINT cart_products_pkey ON cart_products IS 'Первичный ключ, гарантирующий, что один и тот же товар не может быть добавлен в корзину дважды. При повторном добавлении количество должно увеличиваться.';

COMMENT ON CONSTRAINT cart_products_quantity_check ON cart_products IS 'Проверяет, что количество товара положительное. Отрицательное или нулевое количество недопустимо.';

-- ============================================
-- Индексы для производительности
-- ============================================

-- Индекс для быстрого поиска корзины по пользователю
CREATE INDEX idx_shopping_carts_username ON shopping_carts(username);
COMMENT ON INDEX idx_shopping_carts_username IS 'Ускоряет поиск корзины по имени пользователя. Используется во всех операциях API.';

-- Индекс для поиска товаров в корзине
CREATE INDEX idx_cart_products_cart ON cart_products(shopping_cart_id);
COMMENT ON INDEX idx_cart_products_cart IS 'Ускоряет получение всех товаров конкретной корзины.';

-- Индекс для поиска конкретного товара в корзине (покрывающий)
CREATE INDEX idx_cart_products_cart_product ON cart_products(shopping_cart_id, product_id);
COMMENT ON INDEX idx_cart_products_cart_product IS 'Ускоряет проверку наличия конкретного товара в корзине. Используется при добавлении/обновлении товара.';

-- ============================================
-- Права доступа
-- ============================================

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA shopping_cart_schema TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA shopping_cart_schema TO postgres;