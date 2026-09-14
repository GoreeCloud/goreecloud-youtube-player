package com.goreecloud.youtubeplayer.data

import java.io.Closeable

interface LocalLibraryStore : Closeable {
    fun ensureReady()

    fun upsertWatchHistory(entry: WatchHistoryEntry)

    fun upsertResumePosition(entry: ResumePositionEntry)

    fun readSnapshot(exportedAtMs: Long): LibrarySnapshotV1

    fun replaceProgress(snapshot: LibrarySnapshotV1)

    fun summary(): LocalLibrarySummary
}
