CREATE TABLE IF NOT EXISTS account_number_sequence(
    account_type varchar(20) PRIMARY KEY,
    current_value bigint NOT NULL,
    CONSTRAINT account_type_check CHECK (account_type IN ('INDIVIDUAL', 'LEGAL', 'CURRENCY'))
);