CREATE TABLE balance (
    id UUID     PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL,
    version     bigint NOT NULL,
    account_id UUID NOT NULL UNIQUE,
    authorized_balance NUMERIC NOT NULL DEFAULT 0,
    balance     NUMERIC NOT NULL

    CONSTRAINT fk_balance_account FOREIGN KEY (account_id) REFERENCES account(id)
);

CREATE TABLE authorization_balance (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL,
    balance_id UUID NOT NULL,
    amount NUMERIC NOT NULL,
    status VARCHAR(32) NOT NULL

    CONSTRAINT fk_authorization_balance FOREIGN KEY (balance_id) REFERENCES balance(id)
);