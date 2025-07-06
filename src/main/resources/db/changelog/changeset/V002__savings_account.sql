CREATE TABLE savings_account (
     id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
     account_id INT NOT NULL
     balance DECIMAL(15,2) NOT NULL,
     rate_history TEXT,
     last_interest_date DATE,
     version INT NOT NULL DEFAULT 0,
     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     CONSTRAINT fk_savings_account_account FOREIGN KEY (account_id) REFERENCES account(id)
);

CREATE TABLE tariff (
  id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  name VARCHAR(100) NOT NULL
);

CREATE TABLE tariff_rate_history (
  id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  tariff_id BIGINT NOT NULL REFERENCES tariff(id) ON DELETE CASCADE,
  rate NUMERIC(5,2) NOT NULL,
  effective_date DATE NOT NULL,
  CONSTRAINT tariff_rate_history_unique UNIQUE (tariff_id, effective_date)
);
