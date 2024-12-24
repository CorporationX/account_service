CREATE TABLE IF NOT EXISTS public.account_numbers_sequence (
    id BIGSERIAL PRIMARY KEY,
    accounttype VARCHAR(32) NOT NULL UNIQUE,
    current_number BIGINT NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 1
    );
