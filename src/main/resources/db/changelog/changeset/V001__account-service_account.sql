CREATE TABLE IF NOT EXISTS accounts (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_number varchar(20) UNIQUE NOT NULL,
    owner_type varchar(16) NOT NULL,
    owner_id bigint NOT NULL,
    account_type varchar(32) NOT NULL,
    status varchar(16) NOT NULL DEFAULT 'ACTIVE',
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    closed_at timestamptz NULL,
    version bigint NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS balances (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_id bigint NOT NULL UNIQUE,
    actual_balance numeric(19, 4) NOT NULL DEFAULT 0.00,
    currency varchar(3) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    version bigint NOT NULL DEFAULT 1,

    CONSTRAINT uq_balance_account_id UNIQUE (account_id),

    CONSTRAINT fk_balance_account_id FOREIGN KEY (account_id) REFERENCES accounts(id)
);