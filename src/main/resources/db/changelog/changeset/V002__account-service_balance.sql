CREATE TABLE balance (
    id bigserial PRIMARY KEY,
    account_id bigserial NOT NULL,
    authorized_balance numeric(10, 2),
    actual_balance numeric(10, 2),
    created_at timestamp,
    updated_at timestamp,
    version bigint NOT NULL DEFAULT 0,
    FOREIGN KEY (account_id) REFERENCES account (id) ON DELETE CASCADE
);