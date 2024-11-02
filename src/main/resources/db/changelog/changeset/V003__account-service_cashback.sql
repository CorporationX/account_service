CREATE TABLE IF NOT EXISTS cashback_tariff
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(128) NOT NULL UNIQUE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS operation_type_cashback
(
    id             BIGSERIAL PRIMARY KEY,
    operation_type VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS merchant_cashback
(
    id          BIGSERIAL PRIMARY KEY,
    merchant_id VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS cashback_tariff__operation_type_cashback
(
    id                 BIGSERIAL PRIMARY KEY,
    cashback_tariff_id BIGINT REFERENCES cashback_tariff (id),
    operation_type_id  BIGINT REFERENCES operation_type_cashback (id),
    percentage         DECIMAL(5, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS cashback_tariff__merchant_cashback
(
    id                 BIGSERIAL PRIMARY KEY,
    cashback_tariff_id BIGINT REFERENCES cashback_tariff (id),
    merchant_id        BIGINT REFERENCES merchant_cashback (id),
    percentage         DECIMAL(5, 2) NOT NULL
);

INSERT INTO operation_type_cashback (operation_type)
VALUES ('PURCHASE'),
       ('RESTAURANT'),
       ('TRAVEL'),
       ('ENTERTAINMENT'),
       ('TRANSPORT'),
       ('PHARMACY'),
       ('HEALTH_AND_SPORTS'),
       ('CLOTHING_AND_SHOES'),
       ('ELECTRONICS'),
       ('SERVICES'),
       ('EDUCATION');

ALTER TABLE account
    ADD COLUMN IF NOT EXISTS cashback_tariff_id BIGINT REFERENCES cashback_tariff (id);
