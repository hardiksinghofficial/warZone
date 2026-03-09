CREATE TABLE IF NOT EXISTS nuclear_arsenal (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    country_code VARCHAR(3) NOT NULL UNIQUE,
    country_name VARCHAR(100) NOT NULL,
    flag_emoji VARCHAR(10),
    total_warheads INTEGER DEFAULT 0,
    deployed INTEGER DEFAULT 0,
    reserve INTEGER DEFAULT 0,
    retired INTEGER DEFAULT 0,
    icbm_count INTEGER DEFAULT 0,
    slbm_count INTEGER DEFAULT 0,
    strategic_bombers INTEGER DEFAULT 0,
    max_range_km INTEGER DEFAULT 0,
    first_test_year INTEGER,
    last_test_year INTEGER,
    policy VARCHAR(50),
    status VARCHAR(30),
    lat DECIMAL(9,6),
    lng DECIMAL(9,6),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_nuc_country ON nuclear_arsenal(country_code);
