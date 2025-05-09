ALTER TABLE request
ADD COLUMN context TEXT,
ADD COLUMN scheduled_at TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_request_context ON request(context);