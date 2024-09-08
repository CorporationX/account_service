ALTER TABLE balance
ALTER COLUMN authorization_balance_amount TYPE DECIMAL(19, 4) USING authorization_balance_amount::DECIMAL(19, 4);

ALTER TABLE balance
    ALTER COLUMN balance_amount TYPE DECIMAL(19, 4) USING balance_amount::DECIMAL(19, 4);