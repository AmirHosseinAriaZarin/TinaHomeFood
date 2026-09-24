CREATE TABLE IF NOT EXISTS materials (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    current_stock INTEGER NOT NULL,
    minimum_stock INTEGER NOT NULL,
);