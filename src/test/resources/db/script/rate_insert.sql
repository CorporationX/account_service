INSERT INTO account (id, number, user_id, project_id, account_type, currency, status)
VALUES
('11111111-1111-1111-1111-111111111111', '12345678912345', 1, null, 'SAVINGS', 'RUB', 'ACTIVE');

INSERT INTO savings_account(account_id, balance)
VALUES
    ('11111111-1111-1111-1111-111111111111', 250);

INSERT INTO tariff_history(account_id, tariff_id, created_at)
VALUES
    ('11111111-1111-1111-1111-111111111111', 2, '2025-08-01'),
    ('11111111-1111-1111-1111-111111111111', 1, '2025-08-02');