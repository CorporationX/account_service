CREATE TABLE tariff (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        rate_history JSONB NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE savings_account (
                                 id SERIAL PRIMARY KEY,
                                 account_id INT REFERENCES account(id) ON DELETE CASCADE,
                                 tariff_history JSONB NOT NULL,
                                 last_interest_calculation_date DATE,
                                 version INT DEFAULT 1,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
