CREATE TABLE IF NOT EXISTS timeline_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(300) NOT NULL,
    description TEXT,
    event_type VARCHAR(30) NOT NULL,
    severity VARCHAR(20) DEFAULT 'LOW',
    country_code VARCHAR(3),
    lat DECIMAL(9,6),
    lng DECIMAL(9,6),
    event_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    source VARCHAR(50)
);
CREATE INDEX idx_timeline_time ON timeline_events(event_time);
CREATE INDEX idx_timeline_type ON timeline_events(event_type);
