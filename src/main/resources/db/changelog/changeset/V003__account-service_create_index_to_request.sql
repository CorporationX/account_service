CREATE INDEX idx_request_user_id ON request(user_id);

CREATE INDEX idx_request_status ON request(status);

CREATE INDEX idx_request_lock_value ON request(lock_value);

CREATE UNIQUE INDEX unique_open_lock_per_user
    ON request(user_id, lock_value)
    WHERE is_open = true;