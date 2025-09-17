INSERT INTO tariff(type)
VALUES
    ('BASE'),
    ('PROMO'),
    ('SUBSCRIBE');

INSERT INTO tariff_rate_history(tariff_id, rate)
VALUES
    (1, 5),
    (2, 3),
    (3, 5);