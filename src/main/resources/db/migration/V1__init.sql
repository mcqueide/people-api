CREATE TABLE IF NOT EXISTS people (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    birthday DATE,
    address TEXT,
    phone VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_people_name ON people(name);