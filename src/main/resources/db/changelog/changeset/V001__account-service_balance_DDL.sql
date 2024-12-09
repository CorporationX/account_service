CREATE TABLE balance(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    authorization_balance DOUBLE PRECISION,
    actual_balance DOUBLE PRECISION NOT NULL,
    payment_number BIGINT,
    created_at TIMESTAMPTZ DEFAULT current_timestamp NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT current_timestamp,
    version NUMERIC,
    account_id BIGINT NOT NULL,

    CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES  account (id),
    CONSTRAINT chk_authorization_balance CHECK (authorization_balance >= 0),
    CONSTRAINT chk_actual_balance CHECK (actual_balance >= 0)
);