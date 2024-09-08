ALTER TABLE balance
    RENAME COLUMN authorization_balance TO authorization_balance_amount;

ALTER TABLE balance
    RENAME COLUMN balance TO balance_amount;
