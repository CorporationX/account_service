CREATE TABLE IF NOT EXISTS balance(
                                      id BIGSERIAL PRIMARY KEY,
                                      account_id BIGINT,
                                      cur_auth_balance DECIMAL,
                                      cur_fact_balance DECIMAL,
                                      created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                      CONSTRAINT bank_account
                                      FOREIGN KEY(account_id) REFERENCES account(id),
                                      version INT NOT NULL DEFAULT 1
);

CREATE INDEX account_id_index ON balance(account_id);

ALTER TABLE account ADD COLUMN balance_id bigint;