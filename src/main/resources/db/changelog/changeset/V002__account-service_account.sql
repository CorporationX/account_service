CREATE TABLE balance
(
    id              UUID PRIMARY KEY NOT NULL,
    account_id      bigint           NOT NULL,
    auth_balance    NUMERIC(19,2)    NOT NULL,
    current_balance NUMERIC(19,2)    NOT NULL,
    created_at      timestamp,
    updated_at      timestamp,
    version         INT,

    CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES account (id)
);

CREATE TABLE auth_payment
(
    id         UUID PRIMARY KEY NOT NULL,
    balance_id UUID             NOT NULL,
    amount     NUMERIC(19,2)    NOT NULL,
    status     varchar(31),
    created_at timestamp,
    updated_at timestamp,
    version    INT,
    CONSTRAINT fk_reserved_balance_id FOREIGN KEY (balance_id) REFERENCES balance (id)
);
