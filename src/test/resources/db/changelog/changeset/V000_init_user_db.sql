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


INSERT INTO country (title)
VALUES
    ('Russia'),
    ('USA'),
    ('Germany'),
    ('France'),
    ('UK'),
    ('Japan'),
    ('China'),
    ('India'),
    ('Brazil'),
    ('Canada')
ON CONFLICT (title) DO NOTHING;


INSERT INTO users (username, password, email, phone, about_me, active, city, country_id, experience)
VALUES

    ('ivan_petrov',
     '$2a$10$N9qo8uLOickgx2ZMRZoMye3ZJY6QbB6V2vQz2yB5L1L6xH5DqXo9W', -- хеш пароля "password123"
     'ivan.petrov@example.com',
     '+79161234567',
     'Backend developer with 5 years of experience in Java and Spring',
     true,
     'Moscow',
     (SELECT id FROM country WHERE title = 'Russia'),
     5),

    ('anna_smirnova',
     '$2a$10$N9qo8uLOickgx2ZMRZoMye3ZJY6QbB6V2vQz2yB5L1L6xH5DqXo9W',
     'anna.smirnova@example.com',
     '+79162345678',
     'Frontend developer specializing in React and TypeScript',
     true,
     'Saint Petersburg',
     (SELECT id FROM country WHERE title = 'Russia'),
     3),

    ('john_doe',
     '$2a$10$N9qo8uLOickgx2ZMRZoMye3ZJY6QbB6V2vQz2yB5L1L6xH5DqXo9W',
     'john.doe@example.com',
     '+12025551234',
     'Full-stack developer working with Node.js and React',
     true,
     'New York',
     (SELECT id FROM country WHERE title = 'USA'),
     7),

    ('maria_garcia',
     '$2a$10$N9qo8uLOickgx2ZMRZoMye3ZJY6QbB6V2vQz2yB5L1L6xH5DqXo9W',
     'maria.garcia@example.com',
     '+34600123456',
     'DevOps engineer with expertise in Kubernetes and AWS',
     true,
     'Madrid',
     (SELECT id FROM country WHERE title = 'USA'),
     4),

    ('hans_muller',
     '$2a$10$N9qo8uLOickgx2ZMRZoMye3ZJY6QbB6V2vQz2yB5L1L6xH5DqXo9W',
     'hans.muller@example.com',
     '+4915123456789',
     'Data scientist working with Python and machine learning',
     true,
     'Berlin',
     (SELECT id FROM country WHERE title = 'Germany'),
     6),

    ('sophie_bernard',
     '$2a$10$N9qo8uLOickgx2ZMRZoMye3ZJY6QbB6V2vQz2yB5L1L6xH5DqXo9W',
     'sophie.bernard@example.com',
     '+33123456789',
     'Mobile developer for iOS and Android applications',
     true,
     'Paris',
     (SELECT id FROM country WHERE title = 'France'),
     4),

    ('david_wilson',
     '$2a$10$N9qo8uLOickgx2ZMRZoMye3ZJY6QbB6V2vQz2yB5L1L6xH5DqXo9W',
     'david.wilson@example.com',
     '+442071234567',
     'QA engineer with experience in automation testing',
     true,
     'London',
     (SELECT id FROM country WHERE title = 'UK'),
     5),

    ('yuki_tanaka',
     '$2a$10$N9qo8uLOickgx2ZMRZoMye3ZJY6QbB6V2vQz2yB5L1L6xH5DqXo9W',
     'yuki.tanaka@example.com',
     '+81312345678',
     'Game developer using Unity and C#',
     true,
     'Tokyo',
     (SELECT id FROM country WHERE title = 'Japan'),
     3),

    ('li_wei',
     '$2a$10$N9qo8uLOickgx2ZMRZoMye3ZJY6QbB6V2vQz2yB5L1L6xH5DqXo9W',
     'li.wei@example.com',
     '+8613012345678',
     'AI researcher focusing on neural networks',
     true,
     'Beijing',
     (SELECT id FROM country WHERE title = 'China'),
     8),

    ('amit_sharma',
     '$2a$10$N9qo8uLOickgx2ZMRZoMye3ZJY6QbB6V2vQz2yB5L1L6xH5DqXo9W',
     'amit.sharma@example.com',
     '+911234567890',
     'Backend developer with expertise in microservices',
     true,
     'Delhi',
     (SELECT id FROM country WHERE title = 'India'),
     6)
ON CONFLICT (email) DO NOTHING;