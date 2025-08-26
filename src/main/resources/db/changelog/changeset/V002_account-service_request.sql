CREATE TABLE request (
    idp_token UUID PRIMARY KEY,
    user_id BIGINT NOT NULL,
    operation_type VARCHAR(50) NOT NULL,
    lock_value VARCHAR(255),
    is_open BOOLEAN DEFAULT TRUE,
    input_data JSONB,
    status VARCHAR(50),
    status_details TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    version INTEGER DEFAULT 0,

    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT chk_is_open CHECK (is_open IN (TRUE, FALSE))
);
CREATE INDEX idx_user_id ON request(user_id);
CREATE UNIQUE INDEX idx_lock_open ON request(lock_value)
WHERE is_open = TRUE;
CREATE INDEX idx_status ON request(status);
CREATE INDEX idx_user_status ON request(user_id, status);


