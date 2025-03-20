INSERT INTO Account (account_number, owner_id, owner_type, account_type, account_currency)
VALUES ('123456789123', 1, 'USER', 'BUSINESS', 'USD'),
       ('987654321987', 2, 'USER', 'BUSINESS', 'USD');

INSERT INTO balance (id, account_id, auth_balance, current_balance, created_at, updated_at, version)
VALUES ('baa4fdc1-8327-4cb0-b102-f59ff9cbf5d1', 1, 100.00, 100.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       ('baa4fdc1-8327-4cb0-b102-f59ff9cbf5d2', 2, 50.00, 500.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);