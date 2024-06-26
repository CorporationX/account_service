CREATE TABLE request (
                         id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
                         user_id bigint not NULL references users(id),
                         request_type integer not null check (request_type<8),
                         lock_value bigint not NULL,
                         is_open_request boolean not null,
                         input_data varchar(1024) not null,
                         request_status integer not null check (request_status<4),
                         additional_data varchar(1024),
                         created_at TIMESTAMP DEFAULT current_timestamp,
                         updated_at TIMESTAMP DEFAULT current_timestamp,
                         version INT DEFAULT 1
);

CREATE EXTENSION "uuid-ossp";

CREATE UNIQUE INDEX lock_user
    ON request (lock_value, user_id)
    WHERE is_open_request = 'true';