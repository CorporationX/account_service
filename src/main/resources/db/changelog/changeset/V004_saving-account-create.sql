CREATE TABLE IF NOT EXISTS savings_account_rate (
    id          bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    tariff_id   bigint,
    rate        decimal NOT NULL
);

CREATE TABLE IF NOT EXISTS tariff (
    id                          bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    tariff_type                 VARCHAR(64) NOT NULL,
    savings_account_rate_id     bigint NOT NULL,
    CONSTRAINT fk_savings_account_rate_id FOREIGN KEY (savings_account_rate_id) REFERENCES savings_account_rate(id)
);

CREATE TABLE IF NOT EXISTS savings_account (
    id                          bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_number              varchar(64) UNIQUE NOT NULL,
--     tariff_id                   bigint NOT NULL,
--     savings_account_rate_id     decimal NOT NULL,
    last_date_percent           timestamptz,
    version                     bigint DEFAULT 1,
    created_at                  timestamptz DEFAULT current_timestamp,
    updated_at                  timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_account_number FOREIGN KEY (account_number) REFERENCES account (number)
--     CONSTRAINT fk_tariff_id FOREIGN KEY (tariff_id) REFERENCES tariff (id)
);

CREATE TABLE IF NOT EXISTS tariff_history (
    id                          bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    savings_account_id          bigint NOT NULL,
    savings_account_tariff      bigint NOT NULL,
    created_at                  timestamptz DEFAULT current_timestamp,

    CONSTRAINT fr_savings_account_id FOREIGN KEY (savings_account_id) REFERENCES savings_account(id),
    CONSTRAINT fr_savings_account_tariff FOREIGN KEY (savings_account_tariff) REFERENCES tariff(id)
);


