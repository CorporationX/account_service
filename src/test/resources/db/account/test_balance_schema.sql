INSERT INTO accounts_owners(owner_id, owner_type)
VALUES (1, 0)
ON CONFLICT DO NOTHING;

INSERT INTO accounts(account_number, owner_id, account_type, currency, status)
VALUES ('123456789094', 1, 0, 0, 0),
       ('123456789095', 1, 0, 0, 0)
ON CONFLICT DO NOTHING;

ALTER SEQUENCE balance_id_seq RESTART WITH 1;

INSERT INTO balance(account_id, auth_balance, actual_balance)
VALUES (1, 50.00, 100.00)
ON CONFLICT DO NOTHING;