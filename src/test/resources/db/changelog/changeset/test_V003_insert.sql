INSERT INTO users (username)
VALUES
    ('JohnDoe'),
    ('JaneDoe'),
    ('MichaelJohnson');

INSERT INTO project (name)
VALUES
    ('project'),
    ('some project');

INSERT INTO tariff(type)
VALUES
    ('BASE'),
    ('PROMO'),
    ('SUBSCRIBE');

INSERT INTO tariff_rate_history(tariff_id, rate, created_at)
VALUES
    (1, 1, '2025-08-01'),
    (1, 2, '2025-08-02'),
    (2, 3, '2025-08-01'),
    (2, 4, '2025-08-02'),
    (3, 5, '2025-08-01'),
    (3, 6, '2025-08-02');