ALTER TABLE request
ADD COLUMN if not exists context text,
ADD COLUMN if not exists scheduled_at timestamptz;