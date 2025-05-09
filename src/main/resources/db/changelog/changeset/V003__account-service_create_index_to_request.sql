CREATE INDEX IF NOT EXISTS idx_request_user_id ON request(user_id);

CREATE INDEX IF NOT EXISTS idx_request_status ON request(status);

CREATE INDEX IF NOT EXISTS idx_request_lock_value ON request(lock_value);

CREATE INDEX IF NOT EXISTS idx_request_input_data ON request(input_data);

CREATE UNIQUE INDEX IF NOT EXISTS uniq_lock_value_open_request
    ON request(lock_value)
    WHERE is_open = true;