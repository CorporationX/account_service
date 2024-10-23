CREATE TABLE account
(
    id         bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number     varchar(64) UNIQUE NOT NULL,
    project_id bigint,
    user_id    bigint,
    type       varchar(64)        NOT NULL,
    currency   varchar(64)        NOT NULL,
    status     varchar(64)        NOT NULL,
    created_at timestamptz                 DEFAULT current_timestamp,
    updated_at timestamptz                 DEFAULT current_timestamp,
    closed_at  timestamptz,
    version    bigint                      DEFAULT 1
);

CREATE INDEX idx_account_user_id ON account (user_id);

CREATE OR REPLACE FUNCTION generate_random_number()
RETURNS text AS '
DECLARE
    first_digit int;
    remaining_digits int;
BEGIN
    first_digit := floor(random() * 9) + 1;
    remaining_digits := trunc(random() * (20 - 12 + 1) + 12) - 1;
    RETURN concat(first_digit,
                  (SELECT string_agg(floor(random() * 10)::int::text, '''')
                   FROM generate_series(1, remaining_digits)));
END;
' LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION set_account_number()
RETURNS TRIGGER AS '
BEGIN
    IF NEW.number IS NULL THEN
        NEW.number := generate_random_number();
END IF;
RETURN NEW;
END;
' LANGUAGE plpgsql;

CREATE TRIGGER account_number_trigger
    BEFORE INSERT ON account
    FOR EACH ROW
    EXECUTE FUNCTION set_account_number();

CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS '
BEGIN
    NEW.updated_at := current_timestamp;
RETURN NEW;
END;
' LANGUAGE plpgsql;

CREATE TRIGGER update_account_updated_at
    BEFORE UPDATE ON account
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at();

INSERT INTO account (number, user_id, type, currency, status)
VALUES (14534523124, 1, 'PERSONAL', 'RUB', 'ACTIVE'),
       (45927037032456, 1, 'PERSONAL', 'RUB', 'ACTIVE'),
       (2397205732457, 2, 'PERSONAL', 'RUB', 'ACTIVE'),
       (328943571239, 2, 'CURRENCY', 'USD', 'ACTIVE'),
       (59728975298, 3, 'PERSONAL', 'RUB', 'ACTIVE'),
       (549072784387, 3, 'PERSONAL', 'RUB', 'ACTIVE');

INSERT INTO account (number, project_id, type, currency, status)
VALUES (23892656235, 1, 'BUSINESS', 'RUB', 'ACTIVE'),
       (597283728973, 1, 'BUSINESS', 'RUB', 'ACTIVE'),
       (934762365823, 2, 'BUSINESS', 'RUB', 'ACTIVE'),
       (2385627836527863, 2, 'BUSINESS', 'RUB', 'ACTIVE'),
       (923582365862, 3, 'BUSINESS', 'RUB', 'ACTIVE'),
       (934579023752, 3, 'BUSINESS', 'RUB', 'ACTIVE'),
       (934579038845852, 4, 'BUSINESS', 'RUB', 'BLOCKED'),
       (93457903336434, 4, 'BUSINESS', 'RUB', 'BLOCKED');