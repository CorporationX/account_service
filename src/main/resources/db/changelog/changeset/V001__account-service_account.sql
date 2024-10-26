
CREATE TABLE accounts
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    amount     DOUBLE PRECISION                    NOT NULL,
    currency   VARCHAR(8)                          NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version    BIGINT    DEFAULT 0                 NOT NULL
);

CREATE TABLE balances
(
    id                    BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    authorization_balance DOUBLE PRECISION                      NOT NULL,
    actual_balance        DOUBLE PRECISION                      NOT NULL,
    created_at            timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at            timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version               BIGINT      DEFAULT 0                 NOT NULL,
    account_id            BIGINT                                NOT NULL UNIQUE,

    CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES accounts (id)
);
