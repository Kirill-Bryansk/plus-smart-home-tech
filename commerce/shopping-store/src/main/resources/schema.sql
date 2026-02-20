-- ============================================
-- Схема для сервиса shopping-store
-- С подробными комментариями для документирования
-- ============================================

-- Создаем схему, если она не существует
CREATE SCHEMA IF NOT EXISTS shopping_store_schema;

-- Комментарий к схеме
COMMENT ON SCHEMA shopping_store_schema IS 'Схема для микросервиса управления товарами (shopping-store). Содержит информацию о товарах, их категориях и статусах.';

-- Устанавливаем поиск по схеме по умолчанию
SET search_path TO shopping_store_schema;

-- ============================================
-- ENUM типы (создаем как типы PostgreSQL)
-- ============================================

-- Статус количества товара
CREATE TYPE quantity_state AS ENUM ('ENDED', 'FEW', 'ENOUGH', 'MANY');

COMMENT ON TYPE quantity_state IS 'Статус остатка товара на складе:
- ENDED: товар закончился (0 единиц)
- FEW: товара мало (1-5 единиц)
- ENOUGH: товара достаточно (6-50 единиц)
- MANY: товара много (более 50 единиц)';

-- Статус товара
CREATE TYPE product_state AS ENUM ('ACTIVE', 'DEACTIVATE');

COMMENT ON TYPE product_state IS 'Статус товара в магазине:
- ACTIVE: товар активен, отображается в каталоге и доступен для заказа
- DEACTIVATE: товар деактивирован, не отображается в каталоге (скрыт из продажи)';

-- Категория товара
CREATE TYPE product_category AS ENUM ('LIGHTING', 'CONTROL', 'SENSORS');

COMMENT ON TYPE product_category IS 'Категория товара в соответствии с API:
- LIGHTING: освещение (лампы, светильники, LED-ленты)
- CONTROL: управление (контроллеры, пульты, выключатели)
- SENSORS: датчики (движения, температуры, освещенности)';

-- ============================================
-- Таблица товаров
-- ============================================

-- Удаляем таблицу, если она существует
DROP TABLE IF EXISTS shopping_store_schema.product CASCADE;

-- Создаем таблицу product
CREATE TABLE shopping_store_schema.product (
    -- Идентификатор товара в БД (UUID)
    product_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Наименование товара
    product_name VARCHAR(255) NOT NULL,

    -- Описание товара
    description TEXT NOT NULL,

    -- Ссылка на картинку во внешнем хранилище или SVG
    image_src VARCHAR(512),

    -- Статус количества товара (ENDED, FEW, ENOUGH, MANY)
    quantity_state quantity_state NOT NULL,

    -- Статус товара (ACTIVE, DEACTIVATE)
    product_state product_state NOT NULL,

    -- Категория товара (LIGHTING, CONTROL, SENSORS)
    product_category product_category NOT NULL,

    -- Цена товара (минимум 1)
    price NUMERIC(19, 2) NOT NULL CHECK (price >= 1),

    -- Метаданные для аудита
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- КОММЕНТАРИИ К ТАБЛИЦЕ И КОЛОНКАМ
-- ============================================

COMMENT ON TABLE shopping_store_schema.product IS 'Основная таблица товаров интернет-магазина. Хранит всю информацию о товарах, включая название, описание, цену, категорию и статусы. Связана с заказами через сервис shopping-cart.';

COMMENT ON COLUMN shopping_store_schema.product.product_id IS 'Уникальный идентификатор товара в формате UUID. Генерируется автоматически при создании. Используется для ссылок из других сервисов (корзина, заказы). Пример: 550e8400-e29b-41d4-a716-446655440000';

COMMENT ON COLUMN shopping_store_schema.product.product_name IS 'Название товара для отображения в каталоге. Должно быть кратким, но информативным. Максимальная длина - 255 символов. Пример: "Умная лампочка Xiaomi"';

COMMENT ON COLUMN shopping_store_schema.product.description IS 'Подробное описание товара с характеристиками, комплектацией и особенностями. Поддерживает форматирование (HTML/Markdown). Пример: "Светодиодная лампа с поддержкой RGB, управление через Wi-Fi..."';

COMMENT ON COLUMN shopping_store_schema.product.image_src IS 'Путь к изображению товара. Может быть ссылкой на внешнее хранилище (S3, CDN) или относительным путем. Пример: "/images/products/lighting/bulb-001.jpg"';

COMMENT ON COLUMN shopping_store_schema.product.quantity_state IS 'Текущий статус наличия товара на складе. Рассчитывается автоматически на основе данных от warehouse-сервиса. Влияет на отображение в каталоге (бейджи "Мало", "В наличии" и т.д.)';

COMMENT ON COLUMN shopping_store_schema.product.product_state IS 'Статус видимости товара в магазине. ACTIVE - товар продается, DEACTIVATE - товар снят с продажи (не удаляется из БД для сохранения истории заказов)';

COMMENT ON COLUMN shopping_store_schema.product.product_category IS 'Категория товара для фильтрации в каталоге. Соответствует бизнес-направлениям компании: освещение, управление, датчики.';

COMMENT ON COLUMN shopping_store_schema.product.price IS 'Цена товара в рублях с копейками. Минимальное значение - 1 рубль (проверка на уровне БД). Формат: до 19 знаков, из которых 2 после запятой. Пример: 2999.99';

COMMENT ON COLUMN shopping_store_schema.product.created_at IS 'Дата и время добавления товара в систему (в часовом поясе UTC). Заполняется автоматически при вставке. Используется для аналитики и сортировки новинок.';

COMMENT ON COLUMN shopping_store_schema.product.updated_at IS 'Дата и время последнего изменения товара (в часовом поясе UTC). Автоматически обновляется триггером при любых изменениях записи.';

-- ============================================
-- Индексы для оптимизации запросов
-- ============================================

-- Индекс для поиска по категории (часто используется в API)
CREATE INDEX idx_product_category ON shopping_store_schema.product(product_category);
COMMENT ON INDEX idx_product_category IS 'Индекс для ускорения поиска товаров по категории. Используется в API при фильтрации каталога.';

-- Индекс для поиска по статусу
CREATE INDEX idx_product_state ON shopping_store_schema.product(product_state);
COMMENT ON INDEX idx_product_state IS 'Индекс для фильтрации активных/неактивных товаров. Ускоряет запросы, где важно показывать только ACTIVE товары.';

-- Индекс для поиска по статусу количества
CREATE INDEX idx_quantity_state ON shopping_store_schema.product(quantity_state);
COMMENT ON INDEX idx_quantity_state IS 'Индекс для быстрого поиска товаров с определенным статусом наличия (например, "со скидкой на заканчивающиеся").';

-- Составной индекс для частых запросов фильтрации
CREATE INDEX idx_product_category_state ON shopping_store_schema.product(product_category, product_state);
COMMENT ON INDEX idx_product_category_state IS 'Составной индекс для оптимизации запросов, которые фильтруют сразу по категории и статусу (основной сценарий использования каталога).';

-- ============================================
-- Функция для автоматического обновления updated_at
-- ============================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

COMMENT ON FUNCTION update_updated_at_column IS 'Функция-триггер для автоматического обновления поля updated_at при любых изменениях записи. Вызывается BEFORE UPDATE на таблицах, где нужно отслеживать время последнего изменения.';

-- Триггер для автоматического обновления updated_at
CREATE TRIGGER update_product_updated_at
    BEFORE UPDATE ON shopping_store_schema.product
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TRIGGER update_product_updated_at ON shopping_store_schema.product IS 'Триггер, автоматически обновляющий поле updated_at при любых изменениях товара. Срабатывает перед выполнением UPDATE.';

-- ============================================
-- Права доступа
-- ============================================

-- Даем права на схему
GRANT USAGE ON SCHEMA shopping_store_schema TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA shopping_store_schema TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA shopping_store_schema TO postgres;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA shopping_store_schema TO postgres;

COMMENT ON SCHEMA shopping_store_schema IS 'Схема для микросервиса shopping-store. Права на схему выданы пользователю postgres с полным доступом.';

-- ============================================
-- Примеры запросов
-- ============================================

COMMENT ON TABLE shopping_store_schema.product IS 'Основная таблица товаров. Примеры полезных запросов:
-- Получить активные товары категории SENSORS:
SELECT * FROM product WHERE product_category = ''SENSORS'' AND product_state = ''ACTIVE'';
-- Получить товары, которые заканчиваются:
SELECT * FROM product WHERE quantity_state = ''FEW'';
-- Поиск по названию:
SELECT * FROM product WHERE product_name ILIKE ''%лампа%'';
';