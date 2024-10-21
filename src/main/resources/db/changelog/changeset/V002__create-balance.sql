CREATE TABLE IF NOT EXISTS balance(
                                      id bigint PRIMARY KEY,
                                      account_id BIGSERIAL,
                                      cur_auth_balance REAL,
                                      cur_fact_balance REAL,
                                      created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                      CONSTRAINT bank_account
                                      FOREIGN KEY(account_id) REFERENCES account(id),
    version INT NOT NULL DEFAULT 1
    )