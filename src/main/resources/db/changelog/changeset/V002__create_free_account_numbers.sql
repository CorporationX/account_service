CREATE TABLE free_account_numbers
(
    account_type   VARCHAR(4)  NOT NULL,
    account_number VARCHAR(20) NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    PRIMARY KEY (account_type, account_number)
);

CREATE INDEX idx_account_type ON free_account_numbers (account_type);