CREATE TABLE free_account_numbers(
    type VARCHAR(16) NOT NULL,
    number BIGINT NOT NULL,

    CONSTRAINT free_account_numbers_pk PRIMARY KEY (type, number)
);

CREATE TABLE account_numbers_sequence(
    type VARCHAR(16) NOT NULL PRIMARY KEY,
    count BIGINT NOT NULL DEFAULT 0
);
