CREATE TABLE IF NOT EXISTS tariff_rate (
    id          bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    rate        int NOT NULL
);

CREATE TABLE IF NOT EXISTS tariff (
    id          bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    tariff_type VARCHAR(64) NOT NULL,
    history_tariff_rate     int NOT NULL,

    CONSTRAINT fk_tariff_rate_id FOREIGN KEY (history_tariff_rate) REFERENCES tariff_rate (id)
);

CREATE TABLE IF NOT EXISTS tariff_history (
    id          bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
);

CREATE TABLE savings_account
(
    id         bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number     bigint UNIQUE NOT NULL,
    tariff_history bigint NOT NULL,
    last_date_percent    timestamptz,
    version    bigint                      DEFAULT 1,
    created_at timestamptz                 DEFAULT current_timestamp,
    updated_at timestamptz                 DEFAULT current_timestamp,

    CONSTRAINT fk_account_id FOREIGN KEY (number) REFERENCES account (id),
    CONSTRAINT fk_tariff_history_id FOREIGN KEY (tariff_history) REFERENCES tariff (id)
);
