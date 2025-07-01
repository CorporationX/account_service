CREATE TABLE balance (
    id UUID     PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL,
    version     bigint NOT NULL,
    authorized_balance NUMERIC NOT NULL,
    balance     NUMERIC NOT NULL

    CONSTRAINT fk_balance_account FOREIGN KEY (account_id) REFERENCES account(id)
);