--liquibase formatted sql

--changeset kfrolov:create_balance_transfer_20250704
CREATE TABLE balance_transfer (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    authorization_id UUID NOT NULL,

    source_account_id UUID NOT NULL,
    target_account_id UUID NOT NULL,
    amount NUMERIC(30,10) NOT NULL,
    currency VARCHAR(50) NOT NULL,

    category VARCHAR(50) DEFAULT 'NO_CATEGORY' NOT NULL,
    clearing_initiator VARCHAR(50),

    transfer_stage VARCHAR(50) DEFAULT 'PENDING' NOT NULL,
    transfer_status VARCHAR(50) DEFAULT 'IN_PROCESS' NOT NULL,

    created_at TIMESTAMP DEFAULT current_timestamp NOT NULL,
    updated_at TIMESTAMP DEFAULT current_timestamp NOT NULL,

    CONSTRAINT fk_balance_transfer_source_account
        FOREIGN KEY (source_account_id)
        REFERENCES account(id)
        ON DELETE NO ACTION,

    CONSTRAINT fk_balance_transfer_target_account
        FOREIGN KEY (target_account_id)
        REFERENCES account(id)
        ON DELETE NO ACTION
);