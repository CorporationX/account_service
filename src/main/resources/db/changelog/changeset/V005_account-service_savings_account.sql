CREATE TABLE savings_account (
                                 id SERIAL PRIMARY KEY,
                                 account_id INT NOT NULL REFERENCES account(id),
                                 tariff_history JSONB NOT NULL,
                                 last_interest_date DATE NOT NULL,
                                 version INT NOT NULL,
                                 created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                 updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);