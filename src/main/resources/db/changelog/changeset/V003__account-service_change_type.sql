ALTER TABLE request
ALTER COLUMN request_status TYPE smallint USING request_status::smallint;