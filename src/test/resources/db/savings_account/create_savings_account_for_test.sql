INSERT INTO tariff (name, current_rate, created_at, updated_at)
VALUES
    ('bonus', 8, CURRENT_TIMESTAMP - INTERVAL '3 day', CURRENT_TIMESTAMP - INTERVAL '3 day'),
    ('summer', 8, CURRENT_TIMESTAMP - INTERVAL '3 day', CURRENT_TIMESTAMP - INTERVAL '3 day');

INSERT INTO tariff_rate_changelog (tariff_id, rate, change_date)
VALUES
    (1, 10.5, CURRENT_TIMESTAMP - INTERVAL '3 day'),
    (1, 12, CURRENT_TIMESTAMP - INTERVAL '2 day'),
    (1, 8, CURRENT_TIMESTAMP - INTERVAL '1 day'),
    (2, 12, CURRENT_TIMESTAMP - INTERVAL '2 day'),
    (2, 8, CURRENT_TIMESTAMP - INTERVAL '1 day');

INSERT INTO account_owner (owner_id, owner_type)
VALUES
    (1, 'USER'),
    (2, 'PROJECT'),
    (3, 'USER');

INSERT INTO account (account_number, type, currency, status, owner_id)
VALUES
    (5444000000000001, 'SAVINGS', 'RUB', 'ACTIVE', 1),
    (5444000000000002, 'DEBIT', 'EUR', 'ACTIVE', 1),
    (5444000000000003, 'SAVINGS', 'USD', 'ACTIVE', 1);

--INSERT INTO savings_account ()

