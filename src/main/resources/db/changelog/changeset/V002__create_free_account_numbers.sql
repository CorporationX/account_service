CREATE TABLE IF NOT EXISTS public.free_account_numbers (
    id BIGSERIAL PRIMARY KEY,
    account_type VARCHAR(4) NOT NULL,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    CONSTRAINT unique_account_type_number
        UNIQUE (account_type, account_number)
);
