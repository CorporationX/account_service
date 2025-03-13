CREATE TABLE tariff (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(255) NOT NULL,
                        rate_history JSON NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE savings_account (
                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 account_id BIGINT NOT NULL UNIQUE,
                                 balance DECIMAL(19, 4) NOT NULL,
                                 tariff_history JSON NOT NULL,
                                 last_interest_calculation_date DATE,
                                 version BIGINT NOT NULL,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 FOREIGN KEY (account_id) REFERENCES account(id)
);