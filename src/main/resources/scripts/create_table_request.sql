CREATE TYPE request_type_enum AS ENUM ('TRANSFER', 'PAYMENT', 'WITHDRAWAL');
CREATE TYPE request_status_enum AS ENUM ('PENDING', 'PROCESSING', 'COMPLETED', 'CANCELLED', 'FAILED');

Create TABLE request (
  idempotency_token UUID UNIQUE PRIMARY KEY,
  user_id BIGINT NOT NULL,
  request_type request_type_enum NOT NULL,
  lock_key VARCHAR(255) NOT NULL,
  is_open BOOLEAN NOT NULL DEFAULT TRUE,
  input_data JSONB NOT NULL,
  status request_status_enum NOT NULL,
  status_details TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  version BIGINT NOT NULL
);

CREATE INDEX idx_request_user_id ON request(user_id);

CREATE UNIQUE INDEX idx_request_lock_key_open ON request(lock_key) WHERE is_open;
