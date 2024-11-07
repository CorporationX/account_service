CREATE TABLE merchant
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL
);

CREATE TABLE operation_type
(
    id   BIGSERIAL PRIMARY KEY,
    type VARCHAR(128) NOT NULL
);

CREATE TABLE cashback_tariff
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(128) NOT NULL,
    created_at  TIMESTAMP NOT NULL
);

CREATE TABLE operation_cashback
(
    cashback_tariff_id           BIGINT NOT NULL REFERENCES cashback_tariff(id),
    operation_id        BIGINT NOT NULL REFERENCES operation_type(id),
    cashback_percentage DECIMAL(5, 2) NOT NULL,
    version BIGINT DEFAULT 0,
    PRIMARY KEY (cashback_tariff_id, operation_id)
);

CREATE TABLE merchant_cashback
(
    cashback_tariff_id           BIGINT NOT NULL REFERENCES cashback_tariff (id) NOT NULL,
    merchant_id         BIGINT NOT NULL REFERENCES merchant (id) NOT NULL,
    cashback_percentage DECIMAL(5, 2),
    version BIGINT DEFAULT 0,
    PRIMARY KEY (cashback_tariff_id, merchant_id)
);

ALTER TABLE account
    ADD COLUMN cashback_tariff_id BIGINT;

ALTER TABLE account
    ADD CONSTRAINT cashback_tariff_id_fk
        FOREIGN KEY (cashback_tariff_id) REFERENCES cashback_tariff (id);


