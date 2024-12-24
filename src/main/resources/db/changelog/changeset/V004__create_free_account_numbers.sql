CREATE TABLE IF NOT EXISTS public.free_account_numbers (
    id BIGSERIAL PRIMARY KEY,
    accounttype VARCHAR(32) NOT NULL,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    CONSTRAINT unique_account_type_number UNIQUE (accounttype, account_number)
    );
