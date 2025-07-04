--liquibase formatted sql

--changeset kfrolov:create_uuid_extension_20250704
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

--changeset kfrolov:create_balance_table_20250704
CREATE TABLE balance (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    account_id UUID NOT NULL,
    authorized_amount NUMERIC(30, 10) NOT NULL DEFAULT 0,
    actual_amount NUMERIC(30, 10) NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT current_timestamp NOT NULL,
    updated_at TIMESTAMP DEFAULT current_timestamp NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,

    CONSTRAINT chk_balance_amounts_non_negative
        CHECK (authorized_amount >= 0 AND actual_amount >= 0),

    CONSTRAINT fk_balance_account
        FOREIGN KEY (account_id)
        REFERENCES account(id)
        ON DELETE CASCADE
);

--changeset kfrolov:add_idx_balance_account_id_20250704
CREATE INDEX idx_balance_account_id
    ON balance(account_id);