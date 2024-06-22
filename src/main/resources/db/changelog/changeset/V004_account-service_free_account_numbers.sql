CREATE TABLE IF NOT EXISTS free_account_number
(
    id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE                  NOT NULL,
    number          VARCHAR(20) NOT NULL UNIQUE CHECK (LENGTH(number) BETWEEN 12 AND 20)    NOT NULL,
    account_type    VARCHAR(32)                                                             NOT NULL,
    version         BIGINT DEFAULT 1                                                        NOT NULL
);

CREATE UNIQUE INDEX idx_free_account_number_account_type ON free_account_number (account_type);