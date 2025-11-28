CREATE TABLE balance(
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_id bigint NOT NULL,
    authorization_balance bigint DEFAULT 0 NOT NULL,
    actual_balance bigint DEFAULT 0 NOT NULL,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamptz DEFAULT CURRENT_TIMESTAMP,
    version bigint DEFAULT 1,

    CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES account (id)
);
