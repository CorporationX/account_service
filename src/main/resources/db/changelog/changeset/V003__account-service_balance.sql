CREATE TABLE balance (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL UNIQUE,
    authorized_balance NUMERIC(19, 4) NOT NULL DEFAULT 0,
    actual_balance NUMERIC(19, 4) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    version INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT fk_balance_account
        FOREIGN KEY (account_id)
        REFERENCES account (id)
        ON DELETE CASCADE
);
