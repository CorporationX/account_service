CREATE TABLE balance
(
    id                   uuid       primary key,
    authorization_amount bigint     NOT NULL DEFAULT 0,
    actual_amount        bigint     NOT NULL DEFAULT 0,
    account              bigint     NOT NULL UNIQUE
        REFERENCES accounts (id)    ON DELETE CASCADE,
    created_at           timestamptz     DEFAULT current_timestamp NOT NULL,
    updated_at           timestamptz     DEFAULT current_timestamp NOT NULL,
    version              bigint     NOT NULL DEFAULT 0
);
CREATE INDEX account_idx ON balance (account);
CREATE INDEX update_at_idx ON balance (updated_at);