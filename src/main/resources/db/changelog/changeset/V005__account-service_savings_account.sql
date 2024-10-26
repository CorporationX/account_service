CREATE TABLE savings_account
(
    id                        BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id                BIGINT    NOT NULL,
    last_date_before_interest TIMESTAMP NOT NULL,
    version                   BIGINT    NOT NULL,
    date_of_creation          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_of_update            TIMESTAMP          DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES account (id)
);

CREATE TABLE tariff
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    type               INT            NOT NULL,
    current_rate       DECIMAL(19, 4) NOT NULL,
    savings_account_id BIGINT         NOT NULL,
    CONSTRAINT fk_savings_account FOREIGN KEY (savings_account_id) REFERENCES savings_account (id) ON DELETE CASCADE
);

CREATE TABLE tariff_betting_history
(
    tariff_id     BIGINT         NOT NULL,
    betting_value DECIMAL(19, 4) NOT NULL,
    PRIMARY KEY (tariff_id, betting_value),
    CONSTRAINT fk_tariff FOREIGN KEY (tariff_id) REFERENCES tariff (id) ON DELETE CASCADE
);
