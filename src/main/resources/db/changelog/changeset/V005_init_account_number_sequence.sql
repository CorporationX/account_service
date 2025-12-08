INSERT INTO account_number_sequence (type, counter)
VALUES ('CURRENT', 0),
       ('SAVING', 0),
       ('CURRENCY', 0),
       ('CREDIT', 0)
ON CONFLICT (type) DO NOTHING;