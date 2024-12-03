CREATE TABLE rates (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    interest_rate DOUBLE PRECISION NOT NULL
);

CREATE TABLE tariffs (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    tariff_type VARCHAR(24) UNIQUE NOT NULL,
    rate_id BIGINT NOT NULL,
    rate_history JSONB NOT NULL,

    CONSTRAINT fk_tariff_rate FOREIGN KEY (rate_id) REFERENCES rates (id)
);

CREATE TABLE savings_accounts (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number CHAR(36) UNIQUE NOT NULL,
    balance DECIMAL(15, 2) CHECK (balance >= 0) NOT NULL,
    account_id BIGINT NOT NULL,
    owner_id BIGINT NOT NULL,
    tariff_id BIGINT NOT NULL,
    last_interest_date TIMESTAMP,
    version BIGINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tariff_history JSONB NOT NULL,

    CONSTRAINT fk_savings_account_account FOREIGN KEY (account_id) REFERENCES account (id),
    CONSTRAINT fk_savings_account_owner FOREIGN KEY (owner_id) REFERENCES owner (id),
    CONSTRAINT fk_savings_account_tariff FOREIGN KEY (tariff_id) REFERENCES tariffs (id)
);
