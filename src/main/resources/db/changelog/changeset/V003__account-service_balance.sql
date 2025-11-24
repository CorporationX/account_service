CREATE TABLE balance (
    id uuid primary key,
    authorization_amount bigint NOT NULL DEFAULT 0,
    actual_amount bigint NOT NULL DEFAULT 0,
    account_id bigint NOT NULL UNIQUE
        REFERENCES accounts(id) ON DELETE CASCADE,
    created_at timestamptz DEFAULT current_timestamp NOT NULL,
    updated_at timestamptz DEFAULT current_timestamp NOT NULL,
    version bigint NOT NULL DEFAULT 0
);