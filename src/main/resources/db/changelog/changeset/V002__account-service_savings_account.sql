CREATE TABLE savings_account
(
    id                        BIGSERIAL PRIMARY KEY,
    account_id                BIGINT NOT NULL,
    last_date_before_interest TIMESTAMP,
    version                   BIGINT NOT NULL   DEFAULT 1,
    date_of_creation          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_of_update            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account
        FOREIGN KEY (account_id)
            REFERENCES account (id)
            ON DELETE CASCADE
);


CREATE INDEX idx_savings_account_account_id ON savings_account (account_id);


CREATE TABLE tariff
(
    id                 BIGSERIAL PRIMARY KEY,
    type               SMALLINT NOT NULL,
    savings_account_id BIGINT,
    current_rate       NUMERIC(5, 3) NOT NULL,
    CONSTRAINT fk_savings_account
        FOREIGN KEY (savings_account_id)
            REFERENCES savings_account (id)
            ON DELETE CASCADE
);


CREATE TABLE tariff_betting_history
(
    tariff_id     BIGINT        NOT NULL,
    betting_value DECIMAL(5, 2) NOT NULL,
    CONSTRAINT fk_tariff
        FOREIGN KEY (tariff_id)
            REFERENCES tariff (id)
            ON DELETE CASCADE
);