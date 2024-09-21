CREATE TABLE cashback_tariff
(
    id         BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cashback_operation_mapping
(
    id                 BIGSERIAL PRIMARY KEY,
    cashback_tariff_id BIGINT        NOT NULL,
    operation_type     VARCHAR(50)   NOT NULL,
    percentage         NUMERIC(5, 2) NOT NULL,

    FOREIGN KEY (cashback_tariff_id) REFERENCES cashback_tariff (id),
    CONSTRAINT uq_operation_mapping UNIQUE (cashback_tariff_id, operation_type)
);

CREATE TABLE cashback_merchant_mapping
(
    id                 BIGSERIAL PRIMARY KEY,
    cashback_tariff_id BIGINT        NOT NULL,
    merchant_name      VARCHAR(50)   NOT NULL,
    percentage         NUMERIC(5, 2) NOT NULL,

    FOREIGN KEY (cashback_tariff_id) REFERENCES cashback_tariff (id),
    CONSTRAINT uq_merchant_mapping UNIQUE (cashback_tariff_id, merchant_name)
);

ALTER TABLE account
    ADD COLUMN cashback_tariff_id BIGINT;

ALTER TABLE account
    ADD FOREIGN KEY (cashback_tariff_id) REFERENCES cashback_tariff (id);


CREATE INDEX idx_account_cashback_tariff ON account (cashback_tariff_id);

CREATE INDEX idx_cashback_operation_mapping_tariff ON cashback_operation_mapping (cashback_tariff_id);
CREATE INDEX idx_cashback_merchant_mapping_tariff ON cashback_merchant_mapping (cashback_tariff_id);