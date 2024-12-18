CREATE TABLE IF NOT EXISTS free_account_numbers
(
    id BIGSERIAL PRIMARY KEY,
    account_type VARCHAR(32) NOT NULL,
    account_number VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS account_numbers_sequence
(
    id BIGSERIAL PRIMARY KEY,
    account_type VARCHAR(32) NOT NULL,
    current_value BIGINT NOT NULL,
);

INSERT INTO account_numbers_sequence(account_type,current_value)
VALUES  (INDIVIDUAL,0),
        (SAVINGS,0),
        (DEBIT,0),
        (LEGAL,0)