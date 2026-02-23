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

### 2. Запуск микросервисов

Все микросервисы запускаются **без указания профиля** (используется default/PostgreSQL):

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

**Важно:** Сервисы регистрируются в Eureka с **динамическими портами**.

### 3. Проверка работы

#### Eureka Dashboard
Откройте `http://localhost:8761` — должны быть видны все сервисы:
- CONFIG-SERVER
- SHOPPING-STORE
- WAREHOUSE
- SHOPPING-CART

#### Проверка через API

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
│  • CONFIG-SERVER → динамический порт                   │
│  • SHOPPING-STORE → динамический порт                  │
│  • WAREHOUSE → динамический порт                        │
│  • SHOPPING-CART → динамический порт                    │
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
              ┌───────────────┐
              │  PostgreSQL   │
              │  localhost:5432  │
              └───────────────┘
```

---

## База данных

Каждый микросервис имеет **собственную схему** в PostgreSQL:

| Сервис | Схема БД | Таблицы |
|--------|----------|---------|
| **shopping-store** | `shopping_store_schema` | `product` |
| **warehouse** | `warehouse_schema` | `warehouse_product` |
| **shopping-cart** | `shopping_cart_schema` | `shopping_carts`, `cart_products` |

**Базы данных создаются автоматически** при запуске `db-init`.

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
    └── ...
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
```

---

## Дополнительная информация

- **Взаимодействие сервисов**: через Feign-клиенты (REST)
- **Общие DTO**: модуль `interaction-api`
- **Service Discovery**: Eureka
- **Externalized Configuration**: Spring Cloud Config
