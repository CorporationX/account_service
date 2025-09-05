INSERT INTO account_number_sequence(type, counter) VALUES ('PERSONAL', 0)
    ON CONFLICT (type) DO NOTHING;

INSERT INTO account_number_sequence(type, counter) VALUES ('BUSINESS', 0)
    ON CONFLICT (type) DO NOTHING;