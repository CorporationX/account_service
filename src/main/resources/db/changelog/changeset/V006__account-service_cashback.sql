CREATE TABLE cashback_tariff (
    id          UUID NOT NULL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP
);

CREATE TABLE merchant (
    id          UUID NOT NULL PRIMARY KEY,
    user_id     BIGINT,
    project_id  BIGINT,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

    CONSTRAINT fk_merchant_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_merchant_project_id FOREIGN KEY (project_id) REFERENCES project(id)
);

CREATE INDEX idx_cashback_tariff_name ON cashback_tariff (name);
CREATE INDEX idx_cashback_tariff_created_at ON cashback_tariff (created_at);

CREATE TABLE cashback_operation_type (
    id                  UUID NOT NULL PRIMARY KEY,
    cashback_tariff_id  UUID NOT NULL,
    operation_type      VARCHAR(64) NOT NULL,
    cashback_percentage DECIMAL(10,2) NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,

    CONSTRAINT fk_cashback_operation_type_cashback_tariff FOREIGN KEY (cashback_tariff_id) REFERENCES cashback_tariff(id)
);

CREATE INDEX idx_cashback_operation_type_operation_type ON cashback_operation_type(operation_type);
CREATE INDEX idx_cashback_operation_type_cashback_tariff_id_operation_type ON cashback_operation_type(cashback_tariff_id, operation_type);

CREATE TABLE cashback_merchant (
    id                  UUID NOT NULL PRIMARY KEY,
    cashback_tariff_id  UUID NOT NULL,
    merchant_id         UUID NOT NULL,
    cashback_percentage DECIMAL(10,2) NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,

    CONSTRAINT fk_cashback_merchant_cashback_tariff FOREIGN KEY (cashback_tariff_id) REFERENCES cashback_tariff(id),
    CONSTRAINT fk_cashback_merchant_merchant_id FOREIGN KEY (merchant_id) REFERENCES merchant(id)
);

CREATE INDEX idx_cashback_merchant_cashback_tariff_id_merchant_id ON cashback_merchant(cashback_tariff_id, merchant_id);

ALTER TABLE account
  ADD COLUMN cashback_tariff_id UUID;

ALTER TABLE account
  ADD CONSTRAINT fk_account_cashback_tariff FOREIGN KEY (cashback_tariff_id) REFERENCES cashback_tariff(id);

CREATE INDEX idx_auth_payment_status_id_created_at ON auth_payment(status, created_at);
CREATE INDEX idx_account_status ON auth_payment(status);
