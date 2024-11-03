CREATE TABLE payment_operations (
    id                  UUID PRIMARY KEY,
    amount              NUMERIC(12, 2)                          NOT NULL,
    currency            VARCHAR(8)                              NOT NULL,
    account_from        UUID                                    NOT NULL,
    account_to          UUID                                    NOT NULL,
    status              VARCHAR(32)                             NOT NULL,
    created_at          TIMESTAMP DEFAULT current_timestamp     NOT NULL,
    updated_at          TIMESTAMP DEFAULT current_timestamp     NOT NULL,

    CONSTRAINT fk_acc_from  FOREIGN KEY (account_from)  REFERENCES payment_accounts(id),
    CONSTRAINT fk_acc_to    FOREIGN KEY (account_to)    REFERENCES payment_accounts(id)
);