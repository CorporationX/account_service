INSERT INTO public.account_owner (owner_id, owner_type, created_at)
VALUES(1, 'USER', '2024-12-19 10:44:10.991');

INSERT INTO account (account_number, "type", currency, status, created_at, updated_at, closed_at, "version", owner_id)
VALUES ('502058559345', 'INDIVIDUAL', 'EUR', 'ACTIVE', '2024-12-19 10:59:32.583', '2024-12-19 10:59:32.583', NULL, 0, 1);

INSERT INTO balance (account_id, authorization_balance, actual_balance, created_at, updated_at, "version")
VALUES (1, 53.00, 53.00, '2024-12-19 10:59:32.591', '2024-12-19 12:54:14.025', 22);