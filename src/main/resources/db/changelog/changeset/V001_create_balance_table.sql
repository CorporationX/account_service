CREATE TABLE IF NOT EXISTS account_balance (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    available NUMERIC(19,2) NOT NULL DEFAULT 0,
    reserved NUMERIC(19,2) NOT NULL DEFAULT 0,
    currency VARCHAR(3) NOT NULL
    );
