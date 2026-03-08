# Инструкция по запуску проекта

## Требования

- Java 21
- Maven 3.8+
- Docker и Docker Compose

---

## Поэтапный запуск (ручной)

### Шаг 0. Компиляция проекта

**Выполняется один раз** перед первым запуском или после изменений в коде:

```bash
cd C:\Users\Admin\java\plus-smart-home-tech
mvn clean install -DskipTests
```

**Важно:** После компиляции сервисы запускаются **без перекомпиляции** — Maven использует классы из `target/`.

---

### Шаг 1. Docker-инфраструктура

```bash
cd C:\Users\Admin\java\plus-smart-home-tech
docker-compose up -d
docker-compose ps
```

**Сервисы:**
- **PostgreSQL**: `localhost:5432`
- **Kafka**: `localhost:9092`

---

### Шаг 2. Discovery Server (Eureka)

**Откройте НОВЫЙ терминал и выполните:**

```bash
cd C:\Users\Admin\java\plus-smart-home-tech\infra\discovery-server
mvn spring-boot:run
```

**Порт:** `http://localhost:8761`

**Важно:** 
- Оставьте терминал открытым
- Дождитесь: `Started DiscoveryServer in X.XXX seconds`

---

### Шаг 3. Config Server

**Откройте НОВЫЙ терминал и выполните:**

```bash
cd C:\Users\Admin\java\plus-smart-home-tech\infra\config-server
mvn spring-boot:run
```

**Важно:** Оставьте терминал открытым!

---

### Шаг 4. API Gateway

**Откройте НОВЫЙ терминал и выполните:**

```bash
cd C:\Users\Admin\java\plus-smart-home-tech\infra\gateway
mvn spring-boot:run
```

**Порт:** `http://localhost:8080`

---

### Шаг 5. Commerce-сервисы

Каждый сервис запускается в **отдельном терминале**:

```bash
# shopping-store
cd C:\Users\Admin\java\plus-smart-home-tech\commerce\shopping-store
mvn spring-boot:run

# warehouse
cd C:\Users\Admin\java\plus-smart-home-tech\commerce\warehouse
mvn spring-boot:run

# shopping-cart
cd C:\Users\Admin\java\plus-smart-home-tech\commerce\shopping-cart
mvn spring-boot:run

# order
cd C:\Users\Admin\java\plus-smart-home-tech\commerce\order
mvn spring-boot:run

# delivery
cd C:\Users\Admin\java\plus-smart-home-tech\commerce\delivery
mvn spring-boot:run

# payment
cd C:\Users\Admin\java\plus-smart-home-tech\commerce\payment
mvn spring-boot:run
```

---

### Шаг 6. Telemetry-сервисы

Каждый сервис запускается в **отдельном терминале**:

```bash
# collector
cd C:\Users\Admin\java\plus-smart-home-tech\telemetry\collector
mvn spring-boot:run

# aggregator
cd C:\Users\Admin\java\plus-smart-home-tech\telemetry\aggregator
mvn spring-boot:run

# analyzer
cd C:\Users\Admin\java\plus-smart-home-tech\telemetry\analyzer
mvn spring-boot:run
```

---

### Шаг 7. Hub Router

**Откройте НОВЫЙ терминал и выполните:**

```bash
cd C:\Users\Admin\java\plus-smart-home-tech\hub-router
mvn spring-boot:run
```

---

### Шаг 8. Проверка

Откройте в браузере: **http://localhost:8761**

Должны быть видны все сервисы:
- DISCOVERY-SERVER
- CONFIG-SERVER
- GATEWAY
- SHOPPING-STORE
- WAREHOUSE
- SHOPPING-CART
- ORDER
- DELIVERY
- PAYMENT
- COLLECTOR
- AGGREGATOR
- ANALYZER
- HUB-ROUTER

#### Проверка через API

```bash
# Gateway health
curl http://localhost:8080/actuator/health

# shopping-cart
curl "http://localhost:8080/api/v1/shopping-cart?username=test"

# shopping-store
curl "http://localhost:8080/api/v1/shopping-store?category=FOOD&page=0&size=10"

# warehouse
curl "http://localhost:8080/api/v1/warehouse/address"
```

---

## Остановка проекта

### Остановка Java-процессов

В каждом терминале нажмите **Ctrl+C**.

### Остановка Docker

```bash
docker-compose down
```

---

## Альтернатива: запуск через PowerShell скрипты

Для автоматизации можно использовать скрипты в папке `scripts/`.

### Быстрый старт

```powershell
# 0. Компиляция
.\scripts\build.ps1

# 1. Docker
.\scripts\start-infrastructure.ps1

# 2. Eureka
.\scripts\start-discovery.ps1

# 3. Config Server (новый терминал)
.\scripts\start-config.ps1

# 4. Gateway (новый терминал)
.\scripts\start-gateway.ps1

# 5. Commerce (6 сервисов)
.\scripts\start-commerce.ps1

# 6. Telemetry (3 сервиса)
.\scripts\start-telemetry.ps1

# 7. Hub Router (новый терминал)
.\scripts\start-hub-router.ps1
```

### Остановка

```powershell
.\scripts\stop-java.ps1
```

### Скрипты

| Скрипт | Описание |
|--------|----------|
| `build.ps1` | Компиляция проекта |
| `start-infrastructure.ps1` | Запуск Docker |
| `start-discovery.ps1` | Eureka Server |
| `start-config.ps1` | Config Server |
| `start-gateway.ps1` | API Gateway |
| `start-commerce.ps1` | Commerce-сервисы (6 шт) |
| `start-telemetry.ps1` | Telemetry-сервисы (3 шт) |
| `start-hub-router.ps1` | Hub Router |
| `stop-java.ps1` | Остановка Java-процессов |

---

## Архитектура проекта

```
┌─────────────────────────────────────────────────────────┐
│                    Eureka (8761)                        │
│  Реестр сервисов:                                       │
└─────────────────────────────────────────────────────────┘
              ↑
    ┌─────────┴─────────┐
    │                   │
┌───────────┐   ┌───────────┐
│ Config    │   │ Микросер- │
│ Server    │   │ висы      │
│ (dynamic) │   │ (dynamic) │
└───────────┘   └───────────┘
                      ↓
        ┌─────────────┴─────────────┐
        │                           │
┌───────────────┐         ┌─────────────────┐
│  PostgreSQL   │         │     Kafka       │
│  localhost:5432  │         │  localhost:9092 │
└───────────────┘         └─────────────────┘

┌─────────────────────────────────────────────────────────┐
│                    API Gateway (8080)                   │
│  Единая точка входа для всех запросов                   │
└─────────────────────────────────────────────────────────┘
```

---

## База данных

| Сервис | База данных | Схема |
|--------|----------|---------|
| shopping-store | `commerce_shopping_store` | `shopping_store_schema` |
| warehouse | `commerce_warehouse` | `warehouse_schema` |
| shopping-cart | `commerce_shopping_cart` | `shopping_cart_schema` |
| order | `commerce_order` | `order_schema` |
| delivery | `commerce_delivery` | `delivery_schema` |
| payment | `commerce_payment` | `payment_schema` |
| analyzer | `telemetry_analyzer` | `public` |

---

## Kafka-топики

| Топик | Producer | Consumer |
|-------|----------|----------|
| `telemetry.sensors.v1` | Collector | Aggregator |
| `telemetry.snapshots.v1` | Aggregator | Analyzer |
| `telemetry.hubs.v1` | Collector | Analyzer |

---

## Решение проблем

### Скрипты не выполняются (ошибка политики)

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

### Сервис не регистрируется в Eureka

1. Проверьте `http://localhost:8761`
2. Убедитесь, что Eureka и Config Server запущены

### Ошибка подключения к БД

```bash
docker-compose ps
docker-compose logs db-init
```

### Ошибка подключения к Kafka

```bash
docker-compose ps
docker-compose logs kafka
```

### Maven запускает все модули

Запускайте из папки модуля или используйте `-pl`:
```bash
mvn spring-boot:run -pl infra/discovery-server -am
```

---

## Структура проекта

```
plus-smart-home-tech/
├── commerce/              # Commerce-домен
│   ├── shopping-store/    # Каталог товаров
│   ├── warehouse/         # Склад
│   ├── shopping-cart/     # Корзина
│   ├── order/             # Заказы
│   ├── delivery/          # Доставка
│   ├── payment/           # Платежи
│   └── interaction-api/   # Общие DTO
├── telemetry/             # Телеметрия
│   ├── collector/         # Сбор (GRPC)
│   ├── aggregator/        # Агрегация (Kafka)
│   ├── analyzer/          # Анализ (БД)
│   └── serialization/     # Avro
├── hub-router/            # Эмулятор хабов
├── infra/                 # Инфраструктура
│   ├── config-server/     # Config Server
│   ├── discovery-server/  # Eureka
│   └── gateway/           # API Gateway
└── compose.yaml           # Docker
```

---

## Commerce-сервисы: API и взаимодействие

### Схема взаимодействия через Feign

```
┌──────────────────────────────────────────────────────────────────┐
│                        API Gateway (8080)                        │
└──────────────────────────────────────────────────────────────────┘
                              ↓
        ┌─────────────────────┼─────────────────────┐
        ↓                     ↓                     ↓
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│ SHOPPING-     │    │ SHOPPING-     │    │ WAREHOUSE     │
│ CART          │───→│ STORE         │    │               │
│               │    │               │    │               │
│ Feign:        │    │               │    │               │
│ → Warehouse   │    │               │    │               │
└───────────────┘    └───────────────┘    └───────────────┘
                              ↓
                    ┌─────────────────┐
                    │     ORDER       │
                    │                 │
                    │ Feign:          │
                    │ → Warehouse     │
                    │ → Payment       │
                    │ → Delivery      │
                    └────────┬────────┘
                             │
              ┌──────────────┼──────────────┐
              ↓              ↓              ↓
     ┌────────────┐ ┌────────────┐ ┌────────────┐
     │  PAYMENT   │ │  DELIVERY  │ │  WAREHOUSE │
     │            │ │            │ │  (repeat)  │
     │ Feign:     │ │ Feign:     │ │            │
     │ → Order    │ │ → Order    │ │            │
     └────────────┘ └────────────┘ └────────────┘
```

---

### Взаимодействие между сервисами (Feign)

#### Shopping Cart → Warehouse

**Feign клиент:** `WarehouseClient`

```java
@FeignClient(name = "warehouse")
public interface WarehouseClient {
    @PostMapping("/api/v1/warehouse/check")
    ResponseEntity<BookedProductsDto> checkProductQuantityInWarehouse(
            @RequestBody ShoppingCartDto shoppingCart);
}
```

**Circuit Breaker:** Resilience4j с fallback методом (добавление без проверки склада)

---

#### Order → Warehouse, Payment, Delivery

**Feign клиенты:** `WarehouseApi`, `PaymentApi`, `DeliveryApi`

**Вызовы:**
- **Warehouse:** сборка товаров, возврат, передача в доставку
- **Payment:** расчёт стоимости, оплата
- **Delivery:** создание доставки, расчёт стоимости

---

#### Payment → Order

**Feign клиент:** `OrderApi`

**Вызовы:** уведомление об успешной/неуспешной оплате

---

#### Delivery → Order

**Feign клиент:** `OrderApi`

**Вызовы:** уведомление об успешной/неуспешной доставке

---

### API эндпоинты Commerce-сервисов

#### Shopping Store (Каталог товаров)

| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| GET | `/api/v1/shopping-store?category={category}&page={page}&size={size}` | Получить товары категории (пагинация) |
| GET | `/api/v1/shopping-store/{productId}` | Получить товар по ID |
| PUT | `/api/v1/shopping-store` | Создать новый товар |
| POST | `/api/v1/shopping-store` | Обновить товар |
| POST | `/api/v1/shopping-store/removeProductFromStore` | Удалить товар |
| POST | `/api/v1/shopping-store/quantityState?productId={id}&quantityState={state}` | Установить статус количества |

---

#### Shopping Cart (Корзина покупок)

| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| GET | `/api/v1/shopping-cart?username={username}` | Получить корзину пользователя |
| GET | `/api/v1/shopping-cart/{id}` | Получить корзину по идентификатору |
| PUT | `/api/v1/shopping-cart?username={username}` | Добавить товары в корзину |
| DELETE | `/api/v1/shopping-cart?username={username}` | Деактивировать корзину |
| POST | `/api/v1/shopping-cart/remove?username={username}` | Удалить товары из корзины |
| POST | `/api/v1/shopping-cart/change-quantity?username={username}` | Изменить количество товара |

---

#### Warehouse (Склад)

| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| PUT | `/api/v1/warehouse` | Добавить новый товар на склад |
| POST | `/api/v1/warehouse/check` | Проверить наличие товаров для корзины |
| POST | `/api/v1/warehouse/add` | Добавить товар на склад |
| GET | `/api/v1/warehouse/address` | Получить адрес склада |
| POST | `/api/v1/warehouse/assembly?shoppingCartId={id}&orderId={id}` | Собрать товары для заказа |
| POST | `/api/v1/warehouse/shipped?orderId={id}&deliveryId={id}` | Передать в доставку |
| POST | `/api/v1/warehouse/return` | Вернуть товары на склад |

---

#### Order (Заказы)

| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| GET | `/api/v1/order?username={username}` | Получить заказы пользователя |
| PUT | `/api/v1/order` | Создать новый заказ |
| POST | `/api/v1/order/payment` | Оплата заказа |
| POST | `/api/v1/order/payment/failed` | Ошибка оплаты |
| POST | `/api/v1/order/delivery` | Доставка заказа |
| POST | `/api/v1/order/delivery/failed` | Ошибка доставки |
| POST | `/api/v1/order/completed` | Завершение заказа |
| POST | `/api/v1/order/assembly` | Сборка заказа |
| POST | `/api/v1/order/assembly/failed` | Ошибка сборки |
| POST | `/api/v1/order/return` | Возврат заказа |
| POST | `/api/v1/order/calculate/total` | Расчёт общей стоимости |
| POST | `/api/v1/order/calculate/delivery` | Расчёт стоимости доставки |

---

#### Delivery (Доставка)

| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| PUT | `/api/v1/delivery` | Создать доставку |
| POST | `/api/v1/delivery/cost` | Рассчитать стоимость доставки |
| POST | `/api/v1/delivery/in-progress` | Принять в доставку (IN_PROGRESS) |
| POST | `/api/v1/delivery/success` | Успешная доставка |
| POST | `/api/v1/delivery/failed` | Ошибка доставки |

---

#### Payment (Оплата)

| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| POST | `/api/v1/payment/productCost` | Рассчитать стоимость товаров |
| POST | `/api/v1/payment/totalCost` | Рассчитать общую стоимость (товары + доставка + НДС) |
| POST | `/api/v1/payment` | Сформировать оплату |
| POST | `/api/v1/payment/refund` | Успешная оплата (из шлюза) |
| POST | `/api/v1/payment/failed` | Ошибка оплаты (из шлюза) |

---

### Поток данных при создании заказа

```
1. Пользователь → Gateway → Shopping Cart
   └─→ GET /api/v1/shopping-cart?username=user
   
2. Shopping Cart → Warehouse (Feign)
   └─→ POST /api/v1/warehouse/check
       Circuit Breaker: при ошибке → fallback (без проверки)

3. Пользователь → Gateway → Order
   └─→ PUT /api/v1/order (создание заказа)

4. Order → Warehouse (Feign)
   └─→ POST /api/v1/warehouse/assembly?shoppingCartId=...&orderId=...

5. Order → Payment (Feign)
   └─→ POST /api/v1/payment/productCost

6. Order → Delivery (Feign)
   └─→ POST /api/v1/delivery/cost

7. Order → Payment (Feign)
   └─→ POST /api/v1/payment (формирование оплаты)

8. Payment → Order (Feign, callback)
   └─→ POST /api/v1/order/payment (уведомление)

9. Order → Warehouse (Feign)
   └─→ POST /api/v1/warehouse/shipped?orderId=...&deliveryId=...

10. Delivery → Order (Feign, callback)
    └─→ POST /api/v1/order/delivery (уведомление)
```

---

### Resilience4j Circuit Breaker

**Настроен в Shopping Cart для вызовов Warehouse:**

```yaml
resilience4j:
  circuitbreaker:
    instances:
      warehouseClient:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 10000
        permittedNumberOfCallsInHalfOpenState: 3
```

**Fallback:** При недоступности Warehouse товары добавляются в корзину без проверки наличия на складе.

---

### Зависимости между сервисами

| Сервис | Зависит от (Feign) | Используется кем |
|--------|-------------------|------------------|
| **shopping-cart** | warehouse | — |
| **order** | warehouse, payment, delivery | payment, delivery |
| **payment** | order | order |
| **delivery** | order | order |
| **warehouse** | — | shopping-cart, order |
| **shopping-store** | — | — |
