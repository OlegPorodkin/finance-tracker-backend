# Finance Tracker — Backend

REST API for a personal finance tracker. Tracks transactions, categories, budgets, and provides spending analytics.

## Tech Stack

- **Java 21** + **Spring Boot 3.3.0** (virtual threads enabled)
- **PostgreSQL 16** via Docker
- **Spring Security** + **JJWT 0.12.6** — JWT access & refresh token auth
- **Spring Data JPA** + **Hibernate** — ORM (`ddl-auto: validate`, schema owned by Liquibase)
- **Liquibase** — SQL-based migrations
- **Bucket4j 8.10.1** — in-memory rate limiting
- **SpringDoc OpenAPI 2.6.0** — Swagger UI auto-generated from annotations
- **Testcontainers** — integration tests against a real PostgreSQL instance

## Prerequisites

- Java 21
- Maven (or use the included `./mvnw` wrapper)
- Docker (for the local database and integration tests)

## Quick Start

```bash
# 1. Copy environment file
cp .env.example .env
# Edit .env and set JWT_SECRET to a random string of at least 32 characters

# 2. Start PostgreSQL
docker-compose up -d

# 3. Run the application (local profile)
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

The API will be available at `http://localhost:8080`.  
Swagger UI: `http://localhost:8080/swagger-ui.html`

## Environment Variables

| Variable               | Required | Default                 | Description                              |
|------------------------|----------|-------------------------|------------------------------------------|
| `JWT_SECRET`           | Yes      | —                       | Min 32-character secret for signing JWTs |
| `CORS_ALLOWED_ORIGIN`  | No       | `http://localhost:5173` | Frontend origin allowed by CORS          |

## API Overview

| Module       | Base path           | Description                                     |
|--------------|---------------------|-------------------------------------------------|
| Auth         | `/api/auth`         | Register, login, refresh token, logout          |
| Users        | `/api/users`        | Get and update the authenticated user's profile |
| Categories   | `/api/categories`   | CRUD for income/expense categories              |
| Transactions | `/api/transactions` | CRUD for transactions, CSV export               |
| Budgets      | `/api/budgets`      | CRUD for budgets, spending status               |
| Analytics    | `/api/analytics`    | Summary, category breakdown, monthly trends     |

Authentication uses Bearer tokens. Include `Authorization: Bearer <access_token>` on all protected routes.

### Rate Limits (per IP)

| Endpoint                  | Limit        |
|---------------------------|--------------|
| `POST /api/auth/login`    | 5 req / min  |
| `POST /api/auth/register` | 5 req / hour |
| `POST /api/auth/refresh`  | 30 req / min |

## Architecture

The project follows a modular hexagonal (ports & adapters) layout. Each domain module is self-contained:

```
src/main/java/com/financetracker/
├── shared/       # Cross-module types: Money, UserId, PagedResult, exceptions
├── auth/         # Authentication & JWT
├── users/        # User profile management
├── categories/   # Income/expense categories (user-owned + global defaults)
├── transactions/ # Financial transactions
├── budgets/      # Spending budgets with alert thresholds
└── analytics/    # Aggregated reports
```

Within each module:
- `domain/` — entities, value objects, repository interfaces
- `application/` — use cases, DTOs
- `infrastructure/` — JPA adapters, Spring Security config, REST controllers

**Money** is always stored as integer cents (`BIGINT amount_in_cents`) to avoid floating-point errors.

## Database

Migrations live in `src/main/resources/db/changelog/` as Liquibase-formatted SQL files. Register new files in `db.changelog-master.xml`. Never modify existing changesets.

Local connection (matches `docker-compose.yml`):
- Host: `localhost:5432`
- Database: `finance_tracker`
- Credentials: `ft_user` / `ft_password`

## Running Tests

```bash
# All tests (requires Docker for Testcontainers)
./mvnw test

# Single test class
./mvnw test -Dtest=AuthIntegrationTest

# Single test method
./mvnw test -Dtest=AuthIntegrationTest#login_returnsTokens
```

## Build

```bash
./mvnw package -DskipTests
```

The artifact is produced at `target/finance-tracker-backend-0.0.1-SNAPSHOT.jar`.
