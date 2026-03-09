CREATE TABLE IF NOT EXISTS conflicts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(300) NOT NULL,
    description TEXT,
    country_a VARCHAR(100) NOT NULL,
    country_b VARCHAR(100),
    region VARCHAR(100),
    lat DECIMAL(9,6) NOT NULL,
    lng DECIMAL(9,6) NOT NULL,
    severity VARCHAR(20) NOT NULL DEFAULT 'LOW',
    status VARCHAR(30) NOT NULL DEFAULT 'TENSION',
    conflict_type VARCHAR(50),
    casualties INTEGER DEFAULT 0,
    displaced INTEGER DEFAULT 0,
    started_at DATE,
    source VARCHAR(50),
    source_url TEXT,
    external_id VARCHAR(255),
    fetched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_conflict_external UNIQUE (external_id)
);
CREATE INDEX idx_conf_severity ON conflicts(severity);
CREATE INDEX idx_conf_status ON conflicts(status);
CREATE INDEX idx_conf_country_a ON conflicts(country_a);
CREATE INDEX idx_conf_region ON conflicts(region);
CREATE INDEX idx_conf_created ON conflicts(created_at);
