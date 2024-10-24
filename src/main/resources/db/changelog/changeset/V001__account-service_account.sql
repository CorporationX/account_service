CREATE TABLE IF NOT EXISTS free_account_numbers
(
    account_type VARCHAR2(32) NOT NULL,
    account_number NUMERIC(20, 0) not null

    PRIMARY KEY (account_type, account_number)
)

CREATE TABLE IF NOT EXISTS account_numbers_sequence (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    counter bigint not null default 0
    account_type VARCHAR(32) not null,
    version bigint not null default 1
)