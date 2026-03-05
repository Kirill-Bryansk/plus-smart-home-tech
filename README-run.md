# Инструкция по запуску проекта

## Требования

- Java 21
- Maven 3.8+
- Docker и Docker Compose

## Быстрый старт

### 1. Запуск инфраструктуры

```bash
# Запуск PostgreSQL, Kafka, Eureka, Config Server
docker-compose up -d
```

**Сервисы:**
- **PostgreSQL**: `localhost:5432` (БД: telemetry_analyzer, commerce_*)
- **Kafka**: `localhost:9092`
- **Eureka**: `http://localhost:8761`
- **Config Server**: динамический порт (регистрируется в Eureka)

### 2. Запуск API Gateway

```bash
# gateway (единая точка входа для всех запросов)
cd infra/gateway
mvn spring-boot:run
```

**Порт:** `http://localhost:8080`

### 3. Запуск микросервисов

Все микросервисы запускаются **без указания профиля** (используется default/PostgreSQL):

#### Commerce-сервисы

```bash
# shopping-store
cd commerce/shopping-store
mvn spring-boot:run

# warehouse (в отдельном терминале)
cd commerce/warehouse
mvn spring-boot:run

# shopping-cart (в отдельном терминале)
cd commerce/shopping-cart
mvn spring-boot:run
```

#### Telemetry-сервисы

```bash
# collector (GRPC-сервер для приёма телеметрии)
cd telemetry/collector
mvn spring-boot:run

# aggregator (агрегация событий Kafka)
cd telemetry/aggregator
mvn spring-boot:run

# analyzer (анализ и сохранение в БД)
cd telemetry/analyzer
mvn spring-boot:run
```

#### Hub Router (эмулятор хабов и сенсоров)

```bash
cd hub-router
mvn spring-boot:run
```

**Важно:** Сервисы регистрируются в Eureka с **динамическими портами** (кроме Eureka, discovery-server и gateway).

### 4. Проверка работы

#### Eureka Dashboard
Откройте `http://localhost:8761` — должны быть видны все сервисы:
- GATEWAY (порт 8080)
- CONFIG-SERVER
- DISCOVERY-SERVER
- SHOPPING-STORE
- WAREHOUSE
- SHOPPING-CART
- COLLECTOR
- AGGREGATOR
- ANALYZER
- HUB-ROUTER

#### Проверка через API

**Через Gateway (рекомендуется):**

```bash
# shopping-cart: получить корзину
curl "http://localhost:8080/api/v1/shopping-cart?username=test"

# shopping-store: получить товары категории FOOD
curl "http://localhost:8080/api/v1/shopping-store?category=FOOD&page=0&size=10"

# Gateway health
curl http://localhost:8080/actuator/health

# Gateway routes (список маршрутов)
curl http://localhost:8080/actuator/gateway/routes
```

**Напрямую к сервису (если нужно):**

Найдите порт вашего сервиса в Eureka и проверьте:

```bash
# Пример (порт будет другим)
curl http://localhost:XXXXX/actuator/health
```

---

## Архитектура проекта

```
┌─────────────────────────────────────────────────────────┐
│                    Eureka (8761)                        │
│  Реестр сервисов:                                       │
│  • GATEWAY → 8080                                      │
│  • CONFIG-SERVER → динамический порт                   │
│  • SHOPPING-STORE → динамический порт                  │
│  • WAREHOUSE → динамический порт                        │
│  • SHOPPING-CART → динамический порт                    │
│  • COLLECTOR → динамический порт                        │
│  • AGGREGATOR → динамический порт                       │
│  • ANALYZER → динамический порт                         │
│  • HUB-ROUTER → динамический порт                       │
└─────────────────────────────────────────────────────────┘
              ↑
              │
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
│               │         │                 │
│ • telemetry_  │         │ • telemetry.    │
│   analyzer    │         │   sensors.v1    │
│ • commerce_   │         │ • telemetry.    │
│   *_schema    │         │   snapshots.v1  │
│               │         │ • telemetry.    │
│               │         │   hubs.v1       │
└───────────────┘         └─────────────────┘

┌─────────────────────────────────────────────────────────┐
│                    GRPC-сервисы                         │
│  • Collector: localhost:59091 (сервер)                 │
│  • Hub-Router: эмуляция хабов (клиент к Collector)     │
│  • Analyzer → Hub-Router: localhost:59090 (клиент)     │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│                    API Gateway (8080)                   │
│  Единая точка входа для клиентских запросов:           │
│  • /api/v1/shopping-cart/** → SHOPPING-CART            │
│  • /api/v1/shopping-store/** → SHOPPING-STORE          │
│  Маршрутизация через Spring Cloud Gateway              │
└─────────────────────────────────────────────────────────┘
```

### Поток данных Telemetry

```
Hub-Router → (GRPC) → Collector → (Kafka) → Aggregator → (Kafka) → Analyzer → (JPA) → PostgreSQL
                                                              ↓
                                                         (GRPC-клиент)
                                                              ↓
                                                      Hub-Router (обратная связь)
```

---

## База данных

Каждый микросервис имеет **собственную схему** в PostgreSQL:

| Сервис | База данных | Схема | Таблицы |
|--------|----------|---------|---------|
| **shopping-store** | `commerce_shopping_store` | `shopping_store_schema` | `product` |
| **warehouse** | `commerce_warehouse` | `warehouse_schema` | `warehouse_product` |
| **shopping-cart** | `commerce_shopping_cart` | `shopping_cart_schema` | `shopping_carts`, `cart_products` |
| **analyzer** | `telemetry_analyzer` | `public` | `sensor_events`, `hub_events` (из schema.sql) |

**Базы данных создаются автоматически** при запуске `db-init`.

---

## Kafka-топики

| Топик | Описание | Producer | Consumer |
|-------|----------|----------|----------|
| `telemetry.sensors.v1` | События сенсоров | Collector | Aggregator |
| `telemetry.snapshots.v1` | Снэпшоты состояний | Aggregator | Analyzer |
| `telemetry.hubs.v1` | События хабов | Collector | Analyzer |

---

## Конфигурация

### Внешняя конфигурация (Config Server)

Все настройки хранятся в **Config Server**:

```
infra/config-server/src/main/resources/config/
├── commerce/
│   ├── shopping-store/application.yaml
│   ├── warehouse/application.yaml
│   └── shopping-cart/application.yaml
└── telemetry/
    ├── collector/application.yaml
    ├── aggregator/application.yaml
    └── analyzer/application.yaml
```

### Локальная конфигурация

Локальные `application.yaml` в модулях содержат только настройки для подключения к Config Server и Eureka:

```yaml
spring:
  config:
    import: "configserver:"
  cloud:
    config:
      discovery:
        enabled: true
        serviceId: config-server
```

### GRPC-порты

| Сервис | Порт | Тип |
|--------|------|-----|
| **Collector** | 59091 | Сервер |
| **Hub-Router** | - | Клиент (подключается к Collector) |
| **Analyzer** | - | Клиент (подключается к Hub-Router) |

---

## Остановка проекта

```bash
# Остановка всех контейнеров
docker-compose down

# Остановка микросервисов
# Ctrl+C в каждом терминале
```

---

## Решение проблем

### Сервис не регистрируется в Eureka

1. Проверьте, что Eureka запущен: `http://localhost:8761`
2. Проверьте логи сервиса
3. Убедитесь, что Config Server доступен

### Ошибка подключения к БД

1. Проверьте, что PostgreSQL запущен: `docker-compose ps`
2. Проверьте, что БД созданы: `docker exec postgres psql -U postgres -c "\l"`
3. Проверьте логи `db-init`: `docker-compose logs db-init`

### Ошибка подключения к Kafka

1. Проверьте, что Kafka запущен: `docker-compose ps`
2. Проверьте логи: `docker-compose logs kafka`
3. Убедитесь, что топики созданы: `docker-compose logs kafka-init-topics`

### GRPC-ошибки (Collector/Hub-Router)

1. Убедитесь, что Collector запущен первым (порт 59091)
2. Проверьте конфиг `hub-router`: `grpc.collector.host/port`
3. Проверьте конфиг `analyzer`: `grpc.client.hub-router.address`

### Gateway-ошибки (маршрутизация)

1. Проверьте, что сервис зарегистрирован в Eureka: `http://localhost:8761`
2. Проверьте роуты Gateway: `curl http://localhost:8080/actuator/gateway/routes`
3. Убедитесь, что путь запроса совпадает с предикатом роута (`/api/v1/...`)
4. Проверьте логи Gateway с уровнем DEBUG

### Конфликт портов

Все микросервисы используют **динамические порты** (`server.port: 0`). Конфликты исключены.

---

## Логирование

Уровень логирования настроен в `application.yaml` каждого сервиса:

```yaml
logging:
  level:
    ru.yandex.practicum.*: DEBUG
    org.hibernate.SQL: DEBUG
    org.springframework.kafka: INFO
```

---

## Дополнительная информация

### Взаимодействие сервисов

| Тип | Сервисы |
|-----|---------|
| **REST/Feign** | Commerce-сервисы (shopping-cart → warehouse) |
| **GRPC** | Collector ←→ Hub-Router, Analyzer → Hub-Router |
| **Kafka** | Collector → Aggregator → Analyzer |
| **Gateway** | Клиент → Gateway → микросервисы |

### Общие модули

- **commerce/interaction-api** — общие DTO для commerce-сервисов
- **telemetry/serialization** — Avro-сериализация для Kafka

### Service Discovery

- **Eureka** — регистрация и обнаружение сервисов

### Externalized Configuration

- **Spring Cloud Config** — централизованная конфигурация

### API Gateway

- **Spring Cloud Gateway** — единая точка входа, маршрутизация запросов
- Роуты: `/api/v1/shopping-cart/**`, `/api/v1/shopping-store/**`
- CircuitBreaker и Retry для отказоустойчивости

### Circuit Breaker

- **Resilience4j** — в shopping-cart для вызовов warehouse, в gateway для всех маршрутов

---

## Структура проекта

```
plus-smart-home-tech/
├── commerce/              # Commerce-домен
│   ├── shopping-store/    # Каталог товаров
│   ├── warehouse/         # Склад
│   ├── shopping-cart/     # Корзина покупок
│   └── interaction-api/   # Общие DTO
├── telemetry/             # Телеметрия
│   ├── collector/         # Сбор событий (GRPC-сервер)
│   ├── aggregator/        # Агрегация (Kafka)
│   ├── analyzer/          # Анализ и сохранение (БД)
│   └── serialization/     # Avro-сериализация
├── hub-router/            # Эмулятор умного дома
├── grpc-echo-client/      # Пример GRPC-клиента
├── grpc-echo-server/      # Пример GRPC-сервера
├── infra/                 # Инфраструктура
│   ├── config-server/     # Config Server
│   ├── discovery-server/  # Eureka Server
│   └── gateway/           # API Gateway (маршрутизация)
└── compose.yaml           # Docker-инфраструктура
```
