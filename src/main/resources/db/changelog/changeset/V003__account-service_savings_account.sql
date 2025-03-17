-- Тарифы
CREATE TABLE IF NOT EXISTS tariff (
   id               UUID PRIMARY KEY,
   name             VARCHAR(128) NOT NULL UNIQUE,
   rate              DOUBLE PRECISION NOT NULL,
   history          JSONB NOT NULL,
   created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at       TIMESTAMP DEFAULT NULL
);

--Накопительный счет
CREATE TABLE IF NOT EXISTS savings_account (
    id                  UUID PRIMARY KEY,
    account_id          BIGINT UNIQUE NOT NULL,
    tariff_name         VARCHAR(128) NOT NULL,
    balance             DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    last_interest_date  DATE,
    version             INT NOT NULL DEFAULT 0,
    tariff_history      JSONB NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES account(id),
    CONSTRAINT fk_tariff_name FOREIGN KEY (tariff_name) REFERENCES tariff(name)
);