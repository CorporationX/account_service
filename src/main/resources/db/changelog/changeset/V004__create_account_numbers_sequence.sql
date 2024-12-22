CREATE TABLE IF NOT EXISTS public.account_numbers_sequence (
    id BIGSERIAL PRIMARY KEY,
    accounttype VARCHAR(4) NOT NULL UNIQUE,
    current_number BIGINT NOT NULL DEFAULT 0
);
