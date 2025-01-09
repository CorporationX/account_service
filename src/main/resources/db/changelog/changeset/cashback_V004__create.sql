CREATE TABLE cashback_tariff (
                                 id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                                 name VARCHAR(255) NOT NULL,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE operation_type (
                                id BIGSERIAL PRIMARY KEY,
                                operation_type VARCHAR(128) NOT NULL
);

CREATE TABLE cashback_operation_mapping (
                                            id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                                            cashback_tariff_id BIGINT NOT NULL,
                                            operation_type VARCHAR(255) NOT NULL,
                                            cashback_percentage DECIMAL(10,2) NOT NULL,
                                            category VARCHAR(255) NOT NULL,
                                            CONSTRAINT fk_cashback_operation_mapping_cashback_tariff FOREIGN KEY (cashback_tariff_id) REFERENCES cashback_tariff(id)
);

CREATE TABLE merchant (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(128) NOT NULL
);

CREATE TABLE merchant_mapping (
                                  id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                                  cashback_tariff_id BIGINT       NOT NULL,
                                  merchant_id VARCHAR(255) NOT NULL,
                                  cashback_percentage DECIMAL(10,2) NOT NULL,
                                  category VARCHAR(255) NOT NULL,
                                  CONSTRAINT fk_merchant_mapping_cashback_tariff FOREIGN KEY (cashback_tariff_id) REFERENCES cashback_tariff(id)
);

CREATE TABLE transactions (
                              id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                              account_id BIGINT NOT NULL,
                              operation_type VARCHAR(255) NOT NULL,
                              merchant_category VARCHAR(255) NOT NULL,
                              amount DECIMAL(19, 2) NOT NULL,
                              merchant_id VARCHAR(255) NOT NULL,
                              transaction_date DATE NOT NULL
);

ALTER TABLE transactions
    ADD CONSTRAINT fk_account
        FOREIGN KEY (account_id)
            REFERENCES account(id);

ALTER TABLE account
    ADD COLUMN cashback_tariff_id BIGINT;

ALTER TABLE account
    ADD CONSTRAINT fk_account_cashback_tariff FOREIGN KEY (cashback_tariff_id) REFERENCES cashback_tariff(id);


CREATE TABLE operation (
                           id BIGSERIAL PRIMARY KEY,
                           account_id BIGINT NOT NULL,
                           merchant_id BIGINT,
                           operation_type_id BIGINT,
                           amount DECIMAL(19,2) NOT NULL,
                           status VARCHAR(20) NOT NULL,
                           created_at TIMESTAMP NOT NULL,
                           cashback_processed BOOLEAN DEFAULT FALSE,
                           cashback_processed_at TIMESTAMP,
                           CONSTRAINT fk_operation_account
                               FOREIGN KEY (account_id)
                                   REFERENCES account(id),
                           CONSTRAINT fk_operation_merchant
                               FOREIGN KEY (merchant_id)
                                   REFERENCES merchant(id),
                           CONSTRAINT fk_operation_type
                               FOREIGN KEY (operation_type_id)
                                   REFERENCES operation_type(id)
);