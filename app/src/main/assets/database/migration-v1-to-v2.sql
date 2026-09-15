CREATE TABLE IF NOT EXISTS favorites (
    provider_id TEXT NOT NULL,
    provider_video_id TEXT NOT NULL,
    added_at_ms INTEGER NOT NULL,
    PRIMARY KEY (provider_id, provider_video_id)
);

CREATE TABLE IF NOT EXISTS watch_later (
    provider_id TEXT NOT NULL,
    provider_video_id TEXT NOT NULL,
    added_at_ms INTEGER NOT NULL,
    PRIMARY KEY (provider_id, provider_video_id)
);
