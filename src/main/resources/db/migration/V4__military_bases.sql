CREATE TABLE IF NOT EXISTS military_bases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    country_code VARCHAR(3) NOT NULL,
    host_country VARCHAR(3),
    lat DECIMAL(9,6) NOT NULL,
    lng DECIMAL(9,6) NOT NULL,
    base_type VARCHAR(30) NOT NULL,
    personnel INTEGER DEFAULT 0,
    is_overseas BOOLEAN DEFAULT FALSE,
    alliance VARCHAR(50),
    description TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_base_country ON military_bases(country_code);
CREATE INDEX idx_base_type ON military_bases(base_type);
