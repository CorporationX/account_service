CREATE TABLE IF NOT EXISTS tariff_rate (
    id          bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    rate        int NOT NULL
);

CREATE TABLE IF NOT EXISTS tariff (
    id          bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    tariff_type VARCHAR(64) NOT NULL,
    tariff_rate_id     int NOT NULL,

    CONSTRAINT fk_tariff_rate_id FOREIGN KEY (tariff_rate_id) REFERENCES tariff_rate (id)
);

CREATE TABLE IF NOT EXISTS tariff_history (
    id          bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,˚
    tariff_id   bigint NOT NULL,

    CONSTRAINT fk_tariff_id FOREIGN KEY (tariff_id) references tariff (id)
);

CREATE TABLE IF NOT EXISTS savings_account
(
    id         bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number     bigint UNIQUE NOT NULL,
    tariff_history_id bigint NOT NULL,
    last_date_percent    timestamptz,
    version    bigint                      DEFAULT 1,
    created_at timestamptz                 DEFAULT current_timestamp,
    updated_at timestamptz                 DEFAULT current_timestamp,

    CONSTRAINT fk_account_id FOREIGN KEY (number) REFERENCES account (id),
    CONSTRAINT fk_tariff_history_id FOREIGN KEY (tariff_history_id) REFERENCES tariff_history (id)
);
