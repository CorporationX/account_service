INSERT INTO accounts (id, account, balance, owner_id, owner, type, currency, status, created_at, updated_at)
OVERRIDING SYSTEM VALUE
VALUES
    (1, '123456789012', 1000.00, 111, 'USER', 'PAYMENT_ACCOUNT', 'USD', 'ACTIVE', NOW(), NOW()),
    (2, '987654321098', 5000.50, 222, 'PROJECT', 'CURRENCY_ACCOUNT', 'EUR', 'FROZEN', NOW(), NOW()),
    (3, '112233445566', 0.00, 333, 'USER', 'PAYMENT_ACCOUNT', 'RUB', 'CLOSED', NOW(), NOW()),
    (4, '998877665544', 100000.00, 444, 'PROJECT', 'CURRENCY_ACCOUNT', 'USD', 'ACTIVE', NOW(), NOW()),
    (5, '554433221100', 25000.75, 555, 'USER', 'PAYMENT_ACCOUNT', 'RUB', 'ACTIVE', NOW(), NOW());

INSERT INTO balances (id, account_id, auth_balance, actual_balance, created_at, updated_at)
OVERRIDING SYSTEM VALUE
VALUES
    (1, 1, 0.00, 0.00, NOW(), NOW()),
    (2, 2, 1000.50, 950.25, NOW(), NOW()),
    (3, 3, 50000.00, 45000.75, NOW(), NOW()),
    (4, 4, 2000.00, 1800.00, NOW(), NOW()),
    (5, 5, 0.01, 0.01, NOW(), NOW());
