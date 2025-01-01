CREATE TABLE IF NOT EXISTS public.balance(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    authorization_balance DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    actual_balance DECIMAL(15, 2) DEFAULT 0 NOT NULL,
    created_at timestamptz DEFAULT current_timestamp NOT NULL,
    updated_at timestamptz DEFAULT current_timestamp NOT NULL,
    version BIGINT DEFAULT 1 NOT NULL,
    account_id BIGINT UNIQUE NOT NULL,

    CONSTRAINT account_id_fkey
        FOREIGN KEY (account_id) REFERENCES account (id) ON DELETE CASCADE
);