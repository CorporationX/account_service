CREATE TABLE account_balance (
    account_id BIGINT PRIMARY KEY REFERENCES account(id) ON DELETE CASCADE,
    balance NUMERIC(19, 2) NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO account_balance (account_id, balance, version, updated_at)
SELECT 
    id, 
    COALESCE(balance, 0) as balance, 
    0 as version, 
    CURRENT_TIMESTAMP as updated_at
FROM account
WHERE id IS NOT NULL;

