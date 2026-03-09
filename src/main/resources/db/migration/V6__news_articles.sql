CREATE TABLE IF NOT EXISTS news_articles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(500) NOT NULL,
    summary TEXT,
    source_name VARCHAR(100),
    source_url TEXT,
    image_url TEXT,
    category VARCHAR(50),
    region VARCHAR(100),
    published_at TIMESTAMP,
    fetched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    external_hash VARCHAR(64) UNIQUE
);
CREATE INDEX idx_news_published ON news_articles(published_at);
CREATE INDEX idx_news_category ON news_articles(category);
