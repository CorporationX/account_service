ALTER SEQUENCE account_id_seq RESTART WITH 1;
INSERT INTO account(id, name)
VALUES (1, '123456789123456'),
       (2, '1234567891234567');

ALTER SEQUENCE balance_id_seq RESTART WITH 1;
INSERT INTO balance(id, account_id, current_authorization_balance, current_actual_balance, created_at, updated_at,
                    version)
VALUES (1, 1, 23, 23,
        '2024-07-01T14:00:00.0000', '2024-07-01T15:00:00.0000', 1),
       (2, 2, 32, 32,
        '2024-07-01T16:00:00.0000', '2024-07-01T17:00:00.0000', 2);
