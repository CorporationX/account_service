CREATE TABLE scheduled_rate_changes (
    id BIGSERIAL PRIMARY KEY,
    tariff_id BIGINT NOT NULL,
    old_rate DECIMAL(10,2) NOT NULL,
    new_rate DECIMAL(10,2) NOT NULL,
    scheduled_date DATE NOT NULL,
    status VARCHAR(20) CHECK (status IN ('SCHEDULED', 'NOTIFIED', 'COMPLETED')) DEFAULT 'SCHEDULED',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_scheduled_date ON scheduled_rate_changes (scheduled_date);
CREATE INDEX idx_status ON scheduled_rate_changes (status);