ALTER SEQUENCE public.account_id_seq RESTART WITH 1;
INSERT INTO account(name)
VALUES ('123456789123456'),
       ('1234567891234567'),
       ('1234567891234569');

ALTER SEQUENCE balance_id_seq RESTART WITH 1;
INSERT INTO balance(account_id, current_authorization_balance, current_actual_balance, created_at, updated_at,
                    version)
VALUES (1, 23, 23,
        '2024-07-01T14:00:00.0000', '2024-07-01T15:00:00.0000', 1),
       (2, 32, 32,
        '2024-07-01T16:00:00.0000', '2024-07-01T17:00:00.0000', 2),
       (3, 33, 33,
        '2024-07-01T3:00:00.0000', '2024-07-01T4:00:00.0000', 3);

ALTER SEQUENCE balance_audit_id_seq RESTART WITH 1;
INSERT INTO balance_audit(balance_id, balance_version, current_authorization_balance, current_actual_balance,
                          operation_type, audit_at)
VALUES (1, 1, 23, 23, 'TRANSLATION',
        '2024-07-01T14:00:00.0000'),
       (2, 2, 32, 32, 'BOOKING',
        '2024-07-01T16:00:00.0000');
