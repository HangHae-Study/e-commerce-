# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 3.4.1 e-commerce backend (Java 17) with hexagonal architecture. Handles orders, payments, points, products, and coupons.

## Build & Run Commands

```bash
# Infrastructure (MySQL 8.0, Redis 7.2, Kafka, Zookeeper)
docker-compose up -d

# Build
./gradlew build

# Run (requires docker-compose services running)
./gradlew bootRun

# Run all tests (uses Testcontainers - no docker-compose needed)
./gradlew test

# Run a single test class
./gradlew test --tests kr.hhplus.be.server.concurrency.lock.UserPointLockTest

# Run tests matching a pattern
./gradlew test --tests "*.concurrency.*"

# Generate OpenAPI docs (REST Docs → OpenAPI 3 YAML)
./gradlew openapi3
```

## Key Ports

- Application: `localhost:8081`
- MySQL: `localhost:3307` (user: application / password: application / db: hhplus)
- Redis: `localhost:6379`
- Kafka (external): `localhost:9094`
- Kafka UI: `localhost:8090`
- Grafana: `localhost:3003`

## Architecture (Hexagonal / Port-Adapter)

Base package: `kr.hhplus.be.server`

Each domain follows this layered structure with strict one-direction dependency flow:

```
controller           → HTTP request handling, DTO conversion
  ↓
facade (optional)    → Multi-domain orchestration
  ↓
application/service  → Use-case business logic
  ↓
application/repository → Port interfaces (abstractions)
  ↓
adapter/repository   → JPA implementations
adapter/entity       → JPA entities (persistence)
adapter/cache        → Redis caching
adapter/event        → Event publishing
adapter/kafka        → Kafka producers/consumers
```

**Rules:**
- Upper layers never depend on lower layer implementations
- All storage access through repository port interfaces
- No circular dependencies between domains
- Domain/service layers access external storage only via interfaces; adapters provide implementations

## Core Domains

| Domain | Package | Key Responsibility |
|--------|---------|-------------------|
| user | `domain.user` | User info, point balance |
| point | (within user) | Point charge/use/history ledger |
| product | `domain.product` | Product catalog, product lines (SKUs), ranking |
| order | `domain.order` | Order creation, status, order lines |
| payment | `domain.payment` | Payment processing, success/failure states |
| coupon | `domain.coupon` | Coupon issuance, discount application |

## Key Patterns & Infrastructure

- **Distributed Locking**: AOP-based `@DistributedLock` annotation using Redisson (Redis). See `config/aop/lock/`.
- **Optimistic Locking**: JPA `@Version` on Order entity.
- **Event-Driven**: Kafka topics `coupon.issue.v1`, `order.completed.v1` with manual commit (exactly-once semantics).
- **Caching**: Redis-backed top product rankings and coupon issue counts. See `adapter/cache/` packages.
- **Retry**: Spring Retry with `spring-aspects` for transient failure handling.
- **API Docs**: REST Docs snippets generated from tests → OpenAPI 3 YAML via `restdocs-api-spec`.

## Testing

- **Framework**: JUnit 5 + SpringBootTest + Testcontainers (MySQL 8.0, Redis 7.2)
- **All tests run in UTC** (`user.timezone=UTC` system property)
- **Test data**: SQL scripts in `src/test/resources/sql/` loaded via `@Sql` annotations
- **Test categories**: concurrency/lock tests, cache tests, controller integration tests, JPA query tests, Kafka event tests
- **Query monitoring**: `datasource-proxy` for query performance analysis in tests

## Language

Project documentation and comments are written in Korean.
