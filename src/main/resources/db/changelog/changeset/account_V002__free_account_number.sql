CREATE TABLE IF NOT EXISTS free_account_numbers (
                                      id BIGSERIAL PRIMARY KEY,
                                      type VARCHAR(32) NOT NULL,
                                      account_number BIGINT NOT NULL,
                                      UNIQUE (type, account_number)
);

CREATE TABLE IF NOT EXISTS account_numbers_sequence (
                                          type VARCHAR(32) NOT NULL PRIMARY KEY,
                                          counter BIGINT NOT NULL
);