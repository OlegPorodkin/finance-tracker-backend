--liquibase formatted sql

--changeset financetracker:001-users
CREATE TABLE users (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email       VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    currency    VARCHAR(10)  NOT NULL DEFAULT 'USD',
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_users_email ON users (email);

--changeset financetracker:001-refresh-tokens
CREATE TABLE refresh_tokens (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token_hash  VARCHAR(255) NOT NULL UNIQUE,
    user_id     UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    expires_at  TIMESTAMPTZ  NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens (token_hash);
CREATE INDEX idx_refresh_tokens_user_id    ON refresh_tokens (user_id);

--changeset financetracker:001-categories
CREATE TABLE categories (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID         REFERENCES users (id) ON DELETE CASCADE,
    name       VARCHAR(255) NOT NULL,
    type       VARCHAR(20)  NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    color      VARCHAR(20),
    icon       VARCHAR(100),
    is_default BOOLEAN      NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_categories_user_id    ON categories (user_id);
CREATE INDEX idx_categories_type       ON categories (type);
CREATE INDEX idx_categories_is_deleted ON categories (is_deleted);

--changeset financetracker:001-transactions
CREATE TABLE transactions (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    category_id      UUID         REFERENCES categories (id) ON DELETE SET NULL,
    type             VARCHAR(20)  NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    amount_in_cents  BIGINT       NOT NULL CHECK (amount_in_cents > 0),
    currency         VARCHAR(10)  NOT NULL DEFAULT 'USD',
    date             DATE         NOT NULL,
    description      VARCHAR(500) NOT NULL,
    notes            TEXT,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_transactions_user_id     ON transactions (user_id);
CREATE INDEX idx_transactions_category_id ON transactions (category_id);
CREATE INDEX idx_transactions_date        ON transactions (date DESC);
CREATE INDEX idx_transactions_type        ON transactions (type);

--changeset financetracker:001-budgets
CREATE TABLE budgets (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    category_id      UUID         REFERENCES categories (id) ON DELETE CASCADE,
    limit_in_cents   BIGINT       NOT NULL CHECK (limit_in_cents > 0),
    period           VARCHAR(20)  NOT NULL CHECK (period IN ('WEEKLY', 'MONTHLY', 'YEARLY')),
    start_date       DATE         NOT NULL,
    alert_threshold  INT          NOT NULL DEFAULT 80 CHECK (alert_threshold BETWEEN 0 AND 100),
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_budgets_user_id     ON budgets (user_id);
CREATE INDEX idx_budgets_category_id ON budgets (category_id);
