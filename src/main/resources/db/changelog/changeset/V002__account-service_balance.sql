CREATE TABLE balance (
    id bigserial PRIMARY KEY,
    account_id bigint NOT NULL
    authorized_balance numeric(10, 2),
    actual_balance numeric(10, 2),
    created_at timestamp,
    updated_at timestamp,
    version NOT NULL DEFAULT 0,
    FOREIGN KEY (account_id) REFERENCES account (id) ON DELETE CASCADE;
);