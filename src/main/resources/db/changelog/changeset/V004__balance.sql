CREATE TABLE IF NOT EXISTS balance(
    id bigserial PRIMARY KEY,
    account_id bigint NOT NULL UNIQUE,
    authorization_balance NUMERIC(20, 2) NOT NULL DEFAULT 0,
    actual_balance(20, 2) NUMERIC NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version bigint NOT NULL,

    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES account(id)
);