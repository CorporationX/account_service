CREATE TABLE balance
(
    id              UUID PRIMARY KEY,
    account_id      UUID UNIQUE,
    auth_balance    bigint      DEFAULT 0,
    current_balance bigint      DEFAULT 0,
    created_at      timestamptz DEFAULT current_timestamp,
    updated_at      timestamptz DEFAULT current_timestamp,
    version         bigint      DEFAULT 0,

    CONSTRAINT fk_balance_id FOREIGN KEY (account_id) REFERENCES account (id)
);

CREATE TABLE auth_payment
(
    id                UUID PRIMARY KEY,
    source_balance_id UUID UNIQUE,
    target_balance_id UUID UNIQUE,
    amount            bigint NOT NULL,
    status            varchar(31) DEFAULT 'ACTIVE',
    category          varchar(31) DEFAULT 'OTHER',
    created_at        timestamptz DEFAULT current_timestamp,
    updated_at        timestamptz DEFAULT current_timestamp,
    version           bigint      DEFAULT 0,

    CONSTRAINT fk_source_balance_id FOREIGN KEY (source_balance_id) REFERENCES balance (id),
    CONSTRAINT fk_target_balance_id FOREIGN KEY (target_balance_id) REFERENCES balance (id)
);