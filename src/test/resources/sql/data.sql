insert into free_account_numbers(account_type, account_number) values ('INDIVIDUAL', '42000000000000000000');
insert into account_numbers_sequence (account_type) values ('INDIVIDUAL');

alter sequence account_id_seq restart with 1;
insert into account(account_number, owner_id, owner_type, account_type, currency, account_status, version)
values ('42000000000000000004', 1, 'USER', 'INDIVIDUAL', 'USD', 'ACTIVE', 1),
       ('42000000000000000005', 2, 'PROJECT', 'CREDIT_OVERDUE', 'EUR', 'ACTIVE', 1),
       ('42000000000000000006', 5, 'USER', 'INDIVIDUAL', 'RUB', 'ACTIVE', 1);

ALTER SEQUENCE balance_id_seq RESTART WITH 1;
INSERT INTO balance(account_id, current_authorization_balance, current_actual_balance, created_at, updated_at, version)
VALUES (1, 0.00, 100000.00, '2024-06-01 15:00:00.0000',
        '2024-06-01 15:00:00', 0),
       (2, 0.00, 50000.00, '2024-07-01 15:00:00.0000',
        '2024-07-01 15:00:00', 0),
       (3, 50000.00, 150000.00, '2024-07-01 15:00:00.0000',
        '2024-07-01 15:00:00', 0);