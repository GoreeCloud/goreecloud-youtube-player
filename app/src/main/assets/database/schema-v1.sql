PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS videos (
    provider_id TEXT NOT NULL,
    provider_video_id TEXT NOT NULL,
    title TEXT NOT NULL,
    channel_name TEXT,
    duration_seconds INTEGER,
    last_metadata_refresh_ms INTEGER,
    PRIMARY KEY (provider_id, provider_video_id)
);

CREATE TABLE IF NOT EXISTS channel_follows (
    provider_id TEXT NOT NULL,
    provider_channel_id TEXT NOT NULL,
    display_name TEXT NOT NULL,
    rss_url TEXT,
    followed_at_ms INTEGER NOT NULL,
    notifications_enabled INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (provider_id, provider_channel_id)
);

CREATE TABLE IF NOT EXISTS watch_history (
    provider_id TEXT NOT NULL,
    provider_video_id TEXT NOT NULL,
    first_watched_at_ms INTEGER NOT NULL,
    last_watched_at_ms INTEGER NOT NULL,
    play_count INTEGER NOT NULL DEFAULT 1,
    completed INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (provider_id, provider_video_id)
);

CREATE TABLE IF NOT EXISTS resume_positions (
    provider_id TEXT NOT NULL,
    provider_video_id TEXT NOT NULL,
    position_ms INTEGER NOT NULL,
    updated_at_ms INTEGER NOT NULL,
    PRIMARY KEY (provider_id, provider_video_id)
);

CREATE TABLE IF NOT EXISTS playlists (
    playlist_id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    created_at_ms INTEGER NOT NULL,
    updated_at_ms INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS playlist_items (
    playlist_id TEXT NOT NULL,
    position INTEGER NOT NULL,
    provider_id TEXT NOT NULL,
    provider_video_id TEXT NOT NULL,
    added_at_ms INTEGER NOT NULL,
    PRIMARY KEY (playlist_id, position),
    FOREIGN KEY (playlist_id) REFERENCES playlists(playlist_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tags (
    tag_id TEXT PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS video_tags (
    tag_id TEXT NOT NULL,
    provider_id TEXT NOT NULL,
    provider_video_id TEXT NOT NULL,
    PRIMARY KEY (tag_id, provider_id, provider_video_id),
    FOREIGN KEY (tag_id) REFERENCES tags(tag_id) ON DELETE CASCADE
);
