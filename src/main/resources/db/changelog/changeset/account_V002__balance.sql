CREATE TABLE balance
(
    id              UUID PRIMARY KEY NOT NULL,
    account_id      UUID             NOT NULL,
    auth_balance    bigint      DEFAULT 0,
    current_balance bigint      DEFAULT 0,
    created_at      timestamptz DEFAULT current_timestamp,
    updated_at      timestamptz DEFAULT current_timestamp,
    version         bigint      DEFAULT 0,

    CONSTRAINT fk_balance_id FOREIGN KEY (account_id) REFERENCES account (id)
);

CREATE TABLE auth_payment
(
    id         UUID PRIMARY KEY NOT NULL,
    balance_id UUID             NOT NULL,
    amount     bigint           NOT NULL,
    status     varchar(31) DEFAULT 'ACTIVE',
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    version    bigint      DEFAULT 0,

    CONSTRAINT fk_authorization_balance_id FOREIGN KEY (balance_id) REFERENCES balance (id)
);

-- drop table auth_payment;
-- drop table balance