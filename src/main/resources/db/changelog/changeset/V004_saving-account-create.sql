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

CREATE TABLE IF NOT EXISTS savings_account
(
    id         bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number     varchar(64) UNIQUE NOT NULL,
--     tariff_history_id bigint NOT NULL,
    last_date_percent    timestamptz,
    version    bigint                      DEFAULT 1,
    created_at timestamptz                 DEFAULT current_timestamp,
    updated_at timestamptz                 DEFAULT current_timestamp,

    CONSTRAINT fk_account_id FOREIGN KEY (number) REFERENCES account (number)
);

CREATE TABLE IF NOT EXISTS tariff_history (
    id          bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
--     tariff_id   bigint NOT NULL,
    savings_account_id  bigint NOT NULL,

--     CONSTRAINT fk_tariff_id FOREIGN KEY (tariff_id) REFERENCES tariff (id),
    CONSTRAINT fr_savings_account_id FOREIGN KEY (savings_account_id) REFERENCES savings_account(id)
);


