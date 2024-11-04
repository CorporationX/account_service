CREATE TABLE country (
                         id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                         title varchar(64) UNIQUE NOT NULL
);

CREATE TABLE users (
                       id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                       username varchar(64) UNIQUE NOT NULL,
                       password varchar(128) NOT NULL,
                       email varchar(64) UNIQUE NOT NULL,
                       phone varchar(32) UNIQUE,
                       about_me varchar(4096),
                       active boolean DEFAULT true NOT NULL,
                       city varchar(64),
                       country_id bigint NOT NULL,
                       experience int,
                       created_at timestamptz DEFAULT current_timestamp,
                       updated_at timestamptz DEFAULT current_timestamp,

                       CONSTRAINT fk_country_id FOREIGN KEY (country_id) REFERENCES country (id)
);

CREATE TABLE IF NOT EXISTS project
(
    id                BIGSERIAL PRIMARY KEY,
    name              VARCHAR(128) NOT NULL,
    description       VARCHAR(4096),
    parent_project_id BIGINT,
    storage_size      BIGINT,
    max_storage_size  BIGINT,
    owner_id          BIGINT,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status            VARCHAR(255) NOT NULL,
    visibility        VARCHAR(255) NOT NULL,
    cover_image_id    VARCHAR(255),
    CONSTRAINT fk_project_parent FOREIGN KEY (parent_project_id) REFERENCES project (id)
);