INSERT INTO savings_account (account_id, tarrif_history, last_interest_calculation, version)
VALUES
    (1, '[]', NULL, 1),
    (2, '[]', NULL, 1);

INSERT INTO tariff (name, rate_history, created_at, updated_at)
VALUES
    ('Basic', '[5, .5, 4.5]', NOW(), NOW()),
    ('Promotional', '[6.0, 7.0]', NOW(), NOW()),
    ('Subscription', '[4.0, 4.5]', NOW(), NOW());