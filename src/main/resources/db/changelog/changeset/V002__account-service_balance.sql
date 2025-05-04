CREATE TABLE balance (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    authorized_balance DECIMAL(19, 4) NOT NULL DEFAULT 0,
    actual_balance DECIMAL(19, 4) NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version bigint NOT NULL DEFAULT 0,

    CONSTRAINT fk_balance_account FOREIGN KEY (account_id) REFERENCES account(id)
);

CREATE INDEX balance_account_id_idx ON balance (account_id);