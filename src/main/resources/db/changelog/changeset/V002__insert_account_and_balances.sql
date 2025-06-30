INSERT INTO accounts (account_number, owner_type, owner_id, account_type, status, created_at, updated_at, closed_at, version)
VALUES
    ('ACC_INV_0001_USD', 'USER', 1, 'INVESTMENT', 'ACTIVE', NOW(), NOW(), null, 1),
    ('ACC_BIZ_0002_EUR', 'PROJECT', 2, 'BUSINESS', 'ACTIVE', NOW(), NOW(), null, 1),
    ('ACC_SCR_0003_USD', 'USER', 3, 'ESCROW', 'ACTIVE', NOW(), NOW(), null, 1),
    ('ACC_INV_0004_USD', 'USER', 4, 'INVESTMENT', 'CLOSED', NOW() - INTERVAL '10 DAY', null, NOW(),  1);

INSERT INTO balances (account_id, authorized_balance, actual_balance, currency, created_at, updated_at, version)
VALUES
    ((SELECT id FROM accounts WHERE account_number = 'ACC_INV_0001_USD'),0.00, 5000.00, 'USD', NOW() - INTERVAL '2 DAY', NOW(), 1),
    ((SELECT id FROM accounts WHERE account_number = 'ACC_BIZ_0002_EUR'),0.00, 150000.00, 'EUR', NOW() - INTERVAL '1 DAY', NOW(), 1),
    ((SELECT id FROM accounts WHERE account_number = 'ACC_SCR_0003_USD'),0.00, 0.00, 'USD', NOW()- INTERVAL '5 DAY', NOW(), 1),
    ((SELECT id FROM accounts WHERE account_number = 'ACC_INV_0004_USD'), 0.00,0.00, 'USD', NOW() - INTERVAL '10 DAY', NOW(), 1);