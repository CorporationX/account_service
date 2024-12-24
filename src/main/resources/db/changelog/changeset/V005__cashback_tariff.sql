CREATE TABLE cashback_tariff (
    id SERIAL PRIMARY KEY,
    tariff_name varchar(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cashback_operation_mapping (
    id SERIAL PRIMARY KEY,
    tariff_id INT NOT NULL REFERENCES cashback_tariff(id) ON DELETE CASCADE,
    operation_type VARCHAR(255) NOT NULL,
    cashback_percent DECIMAL(5, 2) NOT NULL,
    UNIQUE (tariff_id, operation_type)
);

CREATE TABLE cashback_merchant_mapping (
    id SERIAL PRIMARY KEY,
    tariff_id INT NOT NULL REFERENCES cashback_tariff(id) ON DELETE CASCADE,
    merchant VARCHAR(255) NOT NULL,
    cashback_percent DECIMAL(5, 2) NOT NULL,
    UNIQUE (tariff_id, merchant)
);

CREATE TABLE account_cashback (
    id SERIAL PRIMARY KEY,
    account_id UUID NOT NULL UNIQUE,
    tariff_id INT NOT NULL REFERENCES cashback_tariff(id) ON DELETE CASCADE
);

CREATE INDEX idx_operation_type ON cashback_operation_mapping (operation_type);
CREATE INDEX idx_merchant ON cashback_merchant_mapping (merchant);