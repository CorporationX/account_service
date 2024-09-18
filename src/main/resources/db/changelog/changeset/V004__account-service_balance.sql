CREATE TABLE IF NOT EXISTS balance
(
    id                 UUID PRIMARY KEY NOT NULL,
    authorized_balance DECIMAL DEFAULT 0,
    actual_balance     DECIMAL DEFAULT 0,
    currency           VARCHAR(10),
    created_at         TIMESTAMP WITHOUT TIME ZONE DEFAULT current_timestamp,
    updated_at         TIMESTAMP WITHOUT TIME ZONE DEFAULT current_timestamp,
    version            BIGINT DEFAULT 0,
    account_id         BIGINT
);

ALTER TABLE balance ADD CONSTRAINT fk_balance_account FOREIGN KEY (account_id) REFERENCES account (id);
CREATE UNIQUE INDEX balance_account_id_idx ON balance (account_id);
ALTER TABLE balance ADD CONSTRAINT unique_account_id UNIQUE (account_id);
