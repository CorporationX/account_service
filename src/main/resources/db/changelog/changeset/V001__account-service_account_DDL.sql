create table owner (
    id bigint primary key GENERATED ALWAYS AS IDENTITY UNIQUE,
    name VARCHAR(240) UNIQUE NOT NULL
);

create table type (
    id bigint primary key GENERATED ALWAYS AS IDENTITY UNIQUE,
    name VARCHAR(24) UNIQUE NOT NULL
);

create table account (
    id bigint primary key GENERATED ALWAYS AS IDENTITY UNIQUE,
    number VARCHAR CHECK (LENGTH(number) BETWEEN 12 AND 20) UNIQUE NOT NULL,
    owner_id INT NOT NULL,
    type_id INT NOT NULL,
    currency VARCHAR(5) NOT NULL,
    status VARCHAR(24) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    closed_at timestamptz,
    version NUMERIC,

    CONSTRAINT fk_owner_id FOREIGN KEY (owner_id) REFERENCES owner (id),
    CONSTRAINT fk_type_id FOREIGN KEY (type_id) REFERENCES type (id)
);