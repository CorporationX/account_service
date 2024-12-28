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
