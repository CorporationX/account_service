CREATE TABLE IF NOT EXISTS shedlock (
    name VARCHAR(64) PRIMARY KEY,
    lock_until TIMESTAMP WITH TIME ZONE,
    locked_at TIMESTAMP WITH TIME ZONE,
    locked_by VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS free_account_numbers (
    type varchar(32) NOT NULL,
    account_number BIGINT NOT NULL,
    CONSTRAINT fan_pk PRIMARY KEY (type, account_number)
);

CREATE TABLE IF NOT EXISTS account_number_sequence (
    type varchar(32) NOT NULL,
    counter BIGINT NOT NULL DEFAULT 1,

    CONSTRAINT ans_pk PRIMARY KEY (type)
);

INSERT INTO account_number_sequence (type)
SELECT type FROM (VALUES
    ('INDIVIDUAL'),
    ('BUSINESS'),
    ('CURRENCY'),
    ('SAVINGS'),
    ('CREDIT')
) AS data(type)
WHERE NOT EXISTS (SELECT 1 FROM account_number_sequence WHERE type = data.type);