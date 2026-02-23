-- ============================================
-- Схема для сервиса shopping-store
-- С подробными комментариями для документирования
-- ============================================

-- Создаем схему, если она не существует
CREATE SCHEMA IF NOT EXISTS shopping_store_schema;

-- Комментарий к схеме
COMMENT ON SCHEMA shopping_store_schema IS 'Схема для микросервиса управления товарами (shopping-store). Содержит информацию о товарах, их категориях и статусах.';

-- ============================================
-- Таблица товаров
-- ============================================

-- Создаем таблицу product
CREATE TABLE IF NOT EXISTS shopping_store_schema.product (
    -- Идентификатор товара в БД (UUID)
    product_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Наименование товара
    product_name VARCHAR(255) NOT NULL,

    -- Описание товара
    description TEXT NOT NULL,

    -- Ссылка на картинку во внешнем хранилище или SVG
    image_src VARCHAR(512),

    -- Статус количества товара (ENDED, FEW, ENOUGH, MANY)
    quantity_state VARCHAR(20) NOT NULL CHECK (quantity_state IN ('ENDED', 'FEW', 'ENOUGH', 'MANY')),

    -- Статус товара (ACTIVE, DEACTIVATE)
    product_state VARCHAR(20) NOT NULL CHECK (product_state IN ('ACTIVE', 'DEACTIVATE')),

    -- Категория товара (LIGHTING, CONTROL, SENSORS)
    product_category VARCHAR(20) NOT NULL CHECK (product_category IN ('LIGHTING', 'CONTROL', 'SENSORS')),

    -- Цена товара (минимум 1)
    price NUMERIC(19, 2) NOT NULL,

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

COMMENT ON COLUMN shopping_store_schema.product.price IS 'Цена товара в рублях с копейками. Минимальное значение - 1 рубль. Формат: до 19 знаков, из которых 2 после запятой. Пример: 2999.99';

COMMENT ON COLUMN shopping_store_schema.product.created_at IS 'Дата и время добавления товара в систему (в часовом поясе UTC). Заполняется автоматически при вставке. Используется для аналитики и сортировки новинок.';

COMMENT ON COLUMN shopping_store_schema.product.updated_at IS 'Дата и время последнего изменения товара (в часовом поясе UTC). Обновляется при любых изменениях записи.';

-- ============================================
-- Индексы для оптимизации запросов
-- ============================================

-- Индекс для поиска по категории (часто используется в API)
CREATE INDEX IF NOT EXISTS idx_product_category ON shopping_store_schema.product(product_category);

-- Индекс для поиска по статусу
CREATE INDEX IF NOT EXISTS idx_product_state ON shopping_store_schema.product(product_state);

-- Индекс для поиска по статусу количества
CREATE INDEX IF NOT EXISTS idx_quantity_state ON shopping_store_schema.product(quantity_state);

-- Составной индекс для частых запросов фильтрации
CREATE INDEX IF NOT EXISTS idx_product_category_state ON shopping_store_schema.product(product_category, product_state);
