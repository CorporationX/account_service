CREATE TABLE rate
(
    id           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    rate_percent FLOAT NOT NULL
);

CREATE TABLE savings_account
(
    id                             BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_id                     BIGINT,
    last_interest_calculation_date DATE,
    version                        BIGINT,
    created_at                     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at                     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES account (id)
);

CREATE TABLE tariff
(
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    tariff_type VARCHAR(255) NOT NULL UNIQUE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE tariff_history
(
    id                 BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    tariff_id          BIGINT,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tariff_id) REFERENCES tariff (id)
);

-- Adding rate_history as a separate table to map the relationship between tariff and rate
CREATE TABLE rate_history
(
    tariff_id BIGINT,
    rate_id   BIGINT,
    PRIMARY KEY (tariff_id, rate_id),
    FOREIGN KEY (tariff_id) REFERENCES tariff (id),
    FOREIGN KEY (rate_id) REFERENCES rate (id)
);

-- Insert sample data into rate table
INSERT INTO rate (rate_percent)
VALUES (1.5),
       (2.0),
       (2.5);

-- Insert sample data into tariff table with enum values
INSERT INTO tariff (tariff_type)
VALUES ('BASIC'),
       ('PROMO'),
       ('SUBSCRIPTION'),
       ('FAMILY'),
       ('STUDENT'),
       ('BUSINESS'),
       ('UNLIMITED'),
       ('PREMIUM'),
       ('CUSTOM');

-- Insert sample data into savings_account table
INSERT INTO savings_account (account_id, last_interest_calculation_date, version)
VALUES (1, '2023-01-01', 1),
       (2, '2023-01-02', 1),
       (3, '2023-01-03', 1);

-- Insert sample data into tariff_history table
INSERT INTO tariff_history (savings_account_id, tariff_id)
VALUES (1, 1),
       (2, 2),
       (3, 3);

-- Insert sample data into rate_history table
INSERT INTO rate_history (tariff_id, rate_id)
VALUES (1, 1),
       (1, 2),
       (2, 2),
       (2, 3),
       (3, 1),
       (3, 3);