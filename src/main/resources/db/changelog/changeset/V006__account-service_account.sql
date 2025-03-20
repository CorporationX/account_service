CREATE TABLE rate_change_request
(
     id UUID PRIMARY KEY,
     tariff_id UUID NOT NULL,
     new_rate DECIMAL(5, 2) NOT NULL,
     effective_date DATE NOT NULL,
     request_date DATE NOT NULL DEFAULT NOW(),
     status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
     processed BOOLEAN NOT NULL DEFAULT FALSE,
     UNIQUE (tariff_id, effective_date),
     CONSTRAINT fk_tariff_id FOREIGN KEY (tariff_id) REFERENCES tariff(id)
);