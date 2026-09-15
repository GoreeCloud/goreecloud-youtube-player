package com.goreecloud.youtubeplayer.data

import java.io.Closeable

interface LocalLibraryStore : Closeable {
    fun ensureReady()

    fun upsertWatchHistory(entry: WatchHistoryEntry)

    fun upsertResumePosition(entry: ResumePositionEntry)

    fun upsertFavorite(entry: FavoriteEntry)

    fun upsertWatchLater(entry: WatchLaterEntry)

    fun readSnapshot(exportedAtMs: Long): LibrarySnapshotV2

    fun replaceLibrary(snapshot: LibrarySnapshotV2)

    fun replaceProgress(snapshot: LibrarySnapshotV1) {
        replaceLibrary(snapshot.toV2())
    }

    fun summary(): LocalLibrarySummary
}
