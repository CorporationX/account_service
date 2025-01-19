CREATE TABLE IF NOT EXISTS free_account_numbers (
                                      id BIGSERIAL PRIMARY KEY,
                                      type VARCHAR(32) NOT NULL,
                                      account_number VARCHAR(20) NOT NULL,
                                      UNIQUE (type, account_number)
);

CREATE TABLE IF NOT EXISTS account_numbers_sequence (
                                          type VARCHAR(32) NOT NULL PRIMARY KEY,
                                          counter BIGINT NOT NULL
);