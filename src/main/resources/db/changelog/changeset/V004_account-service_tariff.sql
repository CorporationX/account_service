CREATE TABLE tariff
(
    id           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    name         VARCHAR(255) NOT NULL,
    rate_history JSONB        NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);