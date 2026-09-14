package com.goreecloud.youtubeplayer.data

import org.junit.Assert.assertEquals
import org.junit.Test

class LibraryPortabilityServiceTest {
    @Test
    fun exportAndImport_replaceProgressThroughStoreBoundary() {
        val source = MemoryStore(
            LibrarySnapshotV1(
                exportedAtMs = 0,
                watchHistory = listOf(
                    WatchHistoryEntry("youtube", "video-1", 10, 20, 1, false),
                ),
                resumePositions = listOf(
                    ResumePositionEntry("youtube", "video-1", 5_000, 20),
                ),
            ),
        )
        val exportService = LibraryPortabilityService(source) { 99 }
        val payload = exportService.exportProgress()

        val destination = MemoryStore(
            LibrarySnapshotV1(0, emptyList(), emptyList()),
        )
        val importSummary = LibraryPortabilityService(destination).importProgress(payload)
        val imported = destination.readSnapshot(100)

        assertEquals(1, importSummary.watchHistoryCount)
        assertEquals(1, importSummary.resumePositionCount)
        assertEquals("video-1", imported.watchHistory.single().providerVideoId)
        assertEquals(5_000, imported.resumePositions.single().positionMs)
    }

    private class MemoryStore(
        initialSnapshot: LibrarySnapshotV1,
    ) : LocalLibraryStore {
        private var watchHistory = initialSnapshot.watchHistory
        private var resumePositions = initialSnapshot.resumePositions

        override fun ensureReady() = Unit

        override fun upsertWatchHistory(entry: WatchHistoryEntry) {
            watchHistory = watchHistory.filterNot {
                it.providerId == entry.providerId && it.providerVideoId == entry.providerVideoId
            } + entry
        }

        override fun upsertResumePosition(entry: ResumePositionEntry) {
            resumePositions = resumePositions.filterNot {
                it.providerId == entry.providerId && it.providerVideoId == entry.providerVideoId
            } + entry
        }

        override fun readSnapshot(exportedAtMs: Long): LibrarySnapshotV1 =
            LibrarySnapshotV1(exportedAtMs, watchHistory, resumePositions)

        override fun replaceProgress(snapshot: LibrarySnapshotV1) {
            watchHistory = snapshot.watchHistory
            resumePositions = snapshot.resumePositions
        }

        override fun summary(): LocalLibrarySummary =
            LocalLibrarySummary(1, watchHistory.size, resumePositions.size)

        override fun close() = Unit
    }
}
