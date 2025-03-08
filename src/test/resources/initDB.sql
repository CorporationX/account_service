CREATE TABLE IF NOT EXISTS accounts
(
    id              bigint          PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account         varchar(20)     NOT NULL UNIQUE CHECK (account ~ '^[0-9]{12,20}$'),
    balance         DECIMAL(15,2)   DEFAULT 0.00,
    owner_id        bigint          NOT NULL,
    owner           varchar(20)     NOT NULL,
    type            varchar(50)     NOT NULL,
    currency        varchar(3)      NOT NULL,
    status          varchar(20)     NOT NULL,
    created_at      timestamptz     DEFAULT current_timestamp,
    updated_at      timestamptz     DEFAULT current_timestamp,
    closed_at       timestamptz     DEFAULT NULL,
    account_version bigint
);

CREATE TABLE IF NOT EXISTS balances (
    id SERIAL PRIMARY KEY,
    account_id INT NOT NULL,
    auth_balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    actual_balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    version INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);



INSERT INTO accounts (account, balance, owner_id, owner, type, currency, status, created_at, updated_at, account_version)
VALUES ('123456789012', 1000.00, 111, 'USER', 'PAYMENT_ACCOUNT', 'USD', 'ACTIVE', NOW(), NOW(), 1);
INSERT INTO accounts (account, balance, owner_id, owner, type, currency, status, created_at, updated_at, account_version)
VALUES ('987654321098', 5000.50, 222, 'PROJECT', 'CURRENCY_ACCOUNT', 'EUR', 'FROZEN', NOW(), NOW(), 2);
INSERT INTO accounts (account, balance, owner_id, owner, type, currency, status, created_at, updated_at, closed_at, account_version)
VALUES ('112233445566', 0.00, 333, 'USER', 'PAYMENT_ACCOUNT', 'RUB', 'CLOSED', NOW(), NOW(), NOW(), 3);
INSERT INTO accounts (account, balance, owner_id, owner, type, currency, status, created_at, updated_at, account_version)
VALUES ('998877665544', 100000.00, 444, 'PROJECT', 'CURRENCY_ACCOUNT', 'USD', 'ACTIVE', NOW(), NOW(), 4);
INSERT INTO accounts (account, balance, owner_id, owner, type, currency, status, created_at, updated_at, account_version)
VALUES ('554433221100', 25000.75, 555, 'USER', 'PAYMENT_ACCOUNT', 'RUB', 'ACTIVE', NOW(), NOW(), 5);


INSERT INTO balances (account_id, auth_balance, actual_balance, created_at, updated_at)
VALUES (1, 0.00, 0.00, NOW(), NOW());
INSERT INTO balances (account_id, auth_balance, actual_balance, created_at, updated_at)
VALUES (2, 1000.50, 950.25, NOW(), NOW());
INSERT INTO balances (account_id, auth_balance, actual_balance, created_at, updated_at)
VALUES (3, 50000.00, 45000.75, NOW(), NOW());
INSERT INTO balances (account_id, auth_balance, actual_balance, created_at, updated_at)
VALUES (4, 2000.00, 1800.00, NOW(), NOW());
INSERT INTO balances (account_id, auth_balance, actual_balance, created_at, updated_at)
VALUES (5, 0.01, 0.01, NOW(), NOW());