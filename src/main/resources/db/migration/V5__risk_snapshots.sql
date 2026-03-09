CREATE TABLE IF NOT EXISTS risk_snapshots (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    overall_score DECIMAL(5,2) NOT NULL DEFAULT 0,
    conflict_score DECIMAL(5,2) DEFAULT 0,
    nuclear_score DECIMAL(5,2) DEFAULT 0,
    troop_score DECIMAL(5,2) DEFAULT 0,
    diplomatic_score DECIMAL(5,2) DEFAULT 0,
    level VARCHAR(20) NOT NULL DEFAULT 'LOW',
    top_threat VARCHAR(300),
    active_conflicts INTEGER DEFAULT 0,
    nuclear_nations_involved INTEGER DEFAULT 0,
    calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_risk_time ON risk_snapshots(calculated_at);
