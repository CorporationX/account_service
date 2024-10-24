ALTER SEQUENCE account_id_seq RESTART WITH 1;
INSERT INTO account(account_number)
VALUES ('234312346242'),
       ('153264375432'),
       ('436543678934'),
       ('436543678934');

ALTER SEQUENCE balance_id_seq RESTART WITH 1;
INSERT INTO balance(account_id, current_authorization_balance, current_actual_balance, created_at, updated_at, version)
VALUES (1, 0.00, 100000.00, '2024-06-01 15:00:00.0000',
        '2024-06-01 15:00:00', 0),
       (2, 0.00, 50000.00, '2024-07-01 15:00:00.0000',
        '2024-07-01 15:00:00', 0),
       (3, 50000.00, 150000.00, '2024-07-01 15:00:00.0000',
        '2024-07-01 15:00:00', 0);