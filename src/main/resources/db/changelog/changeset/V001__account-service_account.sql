CREATE TABLE account
(
    id      bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id bigint NOT NULL,

    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE balance
(
    id             bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_id     bigint     NOT NULL,
    auth_balance   bigint,
    actual_balance bigint,
    created_at     timestamptz default current_timestamp,
    last_update_at timestamptz default current_timestamp,
    version        bigint     NOT NULL,
    currency       varchar(4) NOT NULL,

    CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES account (id)
);