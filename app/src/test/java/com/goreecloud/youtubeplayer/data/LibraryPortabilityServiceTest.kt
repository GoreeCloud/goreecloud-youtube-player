package com.goreecloud.youtubeplayer.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class LibraryPortabilityServiceTest {
    @Test
    fun exportAndImport_replaceV2LibraryThroughStoreBoundary() {
        val source = MemoryStore(
            LibrarySnapshotV2(
                exportedAtMs = 0,
                watchHistory = listOf(
                    WatchHistoryEntry("youtube", "video-1", 10, 20, 1, false),
                ),
                resumePositions = listOf(
                    ResumePositionEntry("youtube", "video-1", 5_000, 20),
                ),
                favorites = listOf(FavoriteEntry("youtube", "video-1", 21)),
                watchLater = listOf(WatchLaterEntry("youtube", "video-2", 22)),
            ),
        )
        val payload = LibraryPortabilityService(source) { 99 }.exportLibrary()

        val destination = MemoryStore(emptySnapshot())
        val importSummary = LibraryPortabilityService(destination).importLibrary(payload)
        val imported = destination.readSnapshot(100)

        assertEquals(2, importSummary.sourceVersion)
        assertEquals(1, importSummary.watchHistoryCount)
        assertEquals(1, importSummary.resumePositionCount)
        assertEquals(1, importSummary.favoriteCount)
        assertEquals(1, importSummary.watchLaterCount)
        assertEquals("video-1", imported.watchHistory.single().providerVideoId)
        assertEquals(5_000, imported.resumePositions.single().positionMs)
        assertEquals("video-1", imported.favorites.single().providerVideoId)
        assertEquals("video-2", imported.watchLater.single().providerVideoId)
        assertEquals(1, destination.replaceCount)
    }

    @Test
    fun importLibrary_acceptsV1AndClearsV2OnlyCollectionsUnderReplaceSemantics() {
        val destination = MemoryStore(
            LibrarySnapshotV2(
                exportedAtMs = 0,
                watchHistory = emptyList(),
                resumePositions = emptyList(),
                favorites = listOf(FavoriteEntry("local", "favorite", 1)),
                watchLater = listOf(WatchLaterEntry("local", "later", 2)),
            ),
        )
        val legacyPayload = LibraryInterchangeV1Codec.encode(
            LibrarySnapshotV1(
                exportedAtMs = 99,
                watchHistory = listOf(
                    WatchHistoryEntry("legacy", "video", 10, 20, 1, false),
                ),
                resumePositions = emptyList(),
            ),
        )

        val summary = LibraryPortabilityService(destination).importLibrary(legacyPayload)
        val imported = destination.readSnapshot(100)

        assertEquals(1, summary.sourceVersion)
        assertEquals(1, imported.watchHistory.size)
        assertEquals(emptyList<FavoriteEntry>(), imported.favorites)
        assertEquals(emptyList<WatchLaterEntry>(), imported.watchLater)
        assertEquals(1, destination.replaceCount)
    }

    @Test
    fun importLibrary_rejectedPayloadDoesNotMutateExistingState() {
        val existing = LibrarySnapshotV2(
            exportedAtMs = 0,
            watchHistory = listOf(
                WatchHistoryEntry("local", "existing-video", 10, 20, 1, false),
            ),
            resumePositions = listOf(
                ResumePositionEntry("local", "existing-video", 1_000, 20),
            ),
            favorites = listOf(FavoriteEntry("local", "existing-video", 20)),
            watchLater = emptyList(),
        )
        val destination = MemoryStore(existing)
        val validPayload = LibraryInterchangeV2Codec.encode(
            LibrarySnapshotV2(
                exportedAtMs = 99,
                watchHistory = listOf(
                    WatchHistoryEntry("youtube", "replacement-video", 30, 40, 1, false),
                ),
                resumePositions = emptyList(),
                favorites = emptyList(),
                watchLater = emptyList(),
            ),
        )
        val tamperedPayload = validPayload.replace("\t30\t40\t1\t0", "\t30\t41\t1\t0")

        assertThrows(LibraryInterchangeException::class.java) {
            LibraryPortabilityService(destination).importLibrary(tamperedPayload)
        }

        assertEquals(existing.copy(exportedAtMs = 100), destination.readSnapshot(100))
        assertEquals(0, destination.replaceCount)
    }

    private class MemoryStore(
        initialSnapshot: LibrarySnapshotV2,
    ) : LocalLibraryStore {
        private var snapshot = initialSnapshot
        var replaceCount: Int = 0
            private set

        override fun ensureReady() = Unit

        override fun upsertWatchHistory(entry: WatchHistoryEntry) {
            snapshot = snapshot.copy(
                watchHistory = snapshot.watchHistory.filterNot { sameIdentity(it.providerId, it.providerVideoId, entry.providerId, entry.providerVideoId) } + entry,
            )
        }

        override fun upsertResumePosition(entry: ResumePositionEntry) {
            snapshot = snapshot.copy(
                resumePositions = snapshot.resumePositions.filterNot { sameIdentity(it.providerId, it.providerVideoId, entry.providerId, entry.providerVideoId) } + entry,
            )
        }

        override fun upsertFavorite(entry: FavoriteEntry) {
            snapshot = snapshot.copy(
                favorites = snapshot.favorites.filterNot { sameIdentity(it.providerId, it.providerVideoId, entry.providerId, entry.providerVideoId) } + entry,
            )
        }

        override fun upsertWatchLater(entry: WatchLaterEntry) {
            snapshot = snapshot.copy(
                watchLater = snapshot.watchLater.filterNot { sameIdentity(it.providerId, it.providerVideoId, entry.providerId, entry.providerVideoId) } + entry,
            )
        }

        override fun readSnapshot(exportedAtMs: Long): LibrarySnapshotV2 = snapshot.copy(exportedAtMs = exportedAtMs)

        override fun replaceLibrary(snapshot: LibrarySnapshotV2) {
            replaceCount += 1
            this.snapshot = snapshot
        }

        override fun summary(): LocalLibrarySummary =
            LocalLibrarySummary(
                schemaVersion = 2,
                watchHistoryCount = snapshot.watchHistory.size,
                resumePositionCount = snapshot.resumePositions.size,
                favoriteCount = snapshot.favorites.size,
                watchLaterCount = snapshot.watchLater.size,
            )

        override fun close() = Unit

        private fun sameIdentity(
            providerId: String,
            providerVideoId: String,
            otherProviderId: String,
            otherProviderVideoId: String,
        ): Boolean = providerId == otherProviderId && providerVideoId == otherProviderVideoId
    }

    private fun emptySnapshot(): LibrarySnapshotV2 =
        LibrarySnapshotV2(0, emptyList(), emptyList(), emptyList(), emptyList())
}
