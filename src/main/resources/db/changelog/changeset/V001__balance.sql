--Заглушка - потом удалить!
CREATE TABLE IF NOT EXISTS account
(
    id BIGSERIAL PRIMARY KEY
);
--------------------------------------
CREATE TABLE IF NOT EXISTS balance
(
    id                            BIGSERIAL PRIMARY KEY,
    account_id                    BIGINT REFERENCES account (id) UNIQUE,
    current_authorization_balance DECIMAL(18, 2),
    current_actual_balance        DECIMAL(18, 2),
    created_at                    TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at                    TIMESTAMP NOT NULL DEFAULT NOW(),
    version                       INT       NOT NULL
);