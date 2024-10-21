CREATE TABLE balance
(
    id                    bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    account_id            bigint NOT NULL,
    current_balance       bigint NOT NULL,

    CONSTRAINT fk_balance_account_id FOREIGN KEY (account_id) REFERENCES account (id)
);