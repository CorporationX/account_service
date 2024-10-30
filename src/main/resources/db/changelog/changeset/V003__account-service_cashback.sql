CREATE TABLE IF NOT EXISTS cashback_tariff
(
    id         BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE account
    ADD COLUMN IF NOT EXISTS cashback_tariff_id BIGINT REFERENCES cashback_tariff (id) UNIQUE;

CREATE TABLE IF NOT EXISTS operation_type
(
    id             BIGSERIAL PRIMARY KEY,
    operation_type VARCHAR(32),
    percentage     DECIMAL(5, 2)
);

CREATE TABLE IF NOT EXISTS cashback_tariff_operation_type
(
    cashback_tariff_id BIGINT REFERENCES cashback_tariff (id),
    operation_type_id  BIGINT REFERENCES operation_type (id)
);

CREATE TABLE IF NOT EXISTS merchant
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(256),
    tariff_id    BIGINT REFERENCES cashback_tariff (id),
    operation_id BIGSERIAL REFERENCES operation_type (id) UNIQUE,
    percentage   DECIMAL(5, 2)
);

CREATE TABLE IF NOT EXISTS cashback_tariff_merchant
(
    cashback_tariff_id BIGINT REFERENCES cashback_tariff (id),
    merchant_id        BIGINT REFERENCES merchant (id)
);

CREATE INDEX IF NOT EXISTS idx_tariff_merchant ON merchant (tariff_id, name);


