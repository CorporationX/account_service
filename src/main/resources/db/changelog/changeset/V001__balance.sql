CREATE TABLE IF NOT EXISTS balance
(
    id                            BIGSERIAL PRIMARY KEY,
    account_id                    BIGINT REFERENCES account (id),
    current_authorization_balance DECIMAL(18, 2),
    current_actual_balance        DECIMAL(18, 2),
    created_at                    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version                       INT         NOT NULL
);