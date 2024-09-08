CREATE TABLE balance
(
    id                      BIGINT         NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    authorization_balance   DECIMAL(19,4)  NOT NULL,
    actual_balance          DECIMAL(19,4)  NOT NULL,
    created_at              timestamptz    NOT NULL DEFAULT current_timestamp,
    updated_at              timestamptz    NOT NULL DEFAULT current_timestamp,
    version                 INT            NOT NULL
);


