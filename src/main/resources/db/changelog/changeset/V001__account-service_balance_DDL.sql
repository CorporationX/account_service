CREATE TABLE balance(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    authorization_balance DOUBLE,
    actual_balance DOUBLE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT current_timestamp NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT current_timestamp,
    version NUMERIC,
    account_id BIGINT NOT NULL,

    CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES  account (id)
);