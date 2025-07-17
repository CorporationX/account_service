--liquibase formatted sql

--changeset fagan:create_saving-account_table_20250709
CREATE TABLE savings_account (
    id UUID PRIMARY KEY,--внешний
    balance NUMERIC(30, 10) NOT NULL DEFAULT 0,
    last_interest_accrual_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT current_timestamp NOT NULL,
    updated_at TIMESTAMP DEFAULT current_timestamp NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,

    CONSTRAINT fk_account
        FOREIGN KEY (id)
        REFERENCES account(id),

    CONSTRAINT chk_balance_amounts_non_negative
        CHECK (balance >= 0)
);

--changeset fagan:create_tariff_table_20250709
CREATE TABLE savings_account_tariff (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    current_rate NUMERIC(10, 4) NOT NULL DEFAULT 0.0000
    type VARCHAR(64) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT current_timestamp NOT NULL,
    updated_at TIMESTAMP DEFAULT current_timestamp NOT NULL
);

--changeset fagan:create_saving-account-tariff-history_table_20250709
CREATE TABLE savings_account_tariff_history(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    savings_account_id BIGINT NOT NULL,
    tariff_id BIGINT NOT NULL,
    applied_at TIMESTAMP DEFAULT current_timestamp NOT NULL,

    CONSTRAINT fk_savings_account
        FOREIGN KEY (savings_account_id)
        REFERENCES savings_account(id),

    CONSTRAINT fk_tariff
        FOREIGN KEY (tariff_id)
        REFERENCES  savings_account_tariff(id),

    CONSTRAINT no_updates
        CHECK (false) NO INHERIT
);

--changeset fagan:create_tariff-rate-history_table_20250709
CREATE TABLE tariff_rate_history(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    tariff_id BIGINT NOT NULL,
    rate INT NOT NULL,
    applied_at TIMESTAMP DEFAULT current_timestamp NOT NULL,

    CONSTRAINT no_updates
        CHECK (false) NO INHERIT
);