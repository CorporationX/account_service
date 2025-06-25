CREATE TABLE accounts (
    id bigserial,
    number varchar(32) UNIQUE NOT NULL
        CHECK (LENGTH(number) >= 12 AND LENGTH(number) <= 20),
    owner_type varchar(32) NOT NULL,
    owner_id bigint NOT NULL,
    account_type varchar(64) NOT NULL,
    currency varchar(4) NOT NULL,
    status varchar(32) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    closed_at timestamptz DEFAULT NULL,
    version int NOT NULL
);

CREATE INDEX idx_accounts_owner_id ON accounts(owner_id);
CREATE INDEX idx_accounts_number ON accounts(number);