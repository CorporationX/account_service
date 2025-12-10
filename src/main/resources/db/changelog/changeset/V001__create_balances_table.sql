create TABLE balances (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    account_id uuid NOT NULL UNIQUE,
    auth_balance DECIMAL(19, 4) NOT NULL DEFAULT 0.0000,
    actual_balance DECIMAL(19, 4) NOT NULL DEFAULT 0.0000,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_balance_account FOREIGN KEY (account_id) REFERENCES account (id)
);

create index idx_balance_account_id on balances(account_id);