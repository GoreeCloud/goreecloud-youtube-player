package com.goreecloud.youtubeplayer.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SQLiteLocalLibraryStoreInstrumentedTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.deleteDatabase(SQLiteLocalLibraryStore.DATABASE_NAME)
    }

    @After
    fun tearDown() {
        context.deleteDatabase(SQLiteLocalLibraryStore.DATABASE_NAME)
    }

    @Test
    fun schemaV2InitializesAndLibraryStatePersistsAcrossReopen() {
        SQLiteLocalLibraryStore(context).use { store ->
            store.ensureReady()
            assertEquals(
                LocalLibrarySummary(
                    schemaVersion = 2,
                    watchHistoryCount = 0,
                    resumePositionCount = 0,
                    favoriteCount = 0,
                    watchLaterCount = 0,
                ),
                store.summary(),
            )

            store.upsertWatchHistory(
                WatchHistoryEntry("runtime", "persisted-video", 100, 200, 2, false),
            )
            store.upsertResumePosition(
                ResumePositionEntry("runtime", "persisted-video", 12_345, 200),
            )
            store.upsertFavorite(
                FavoriteEntry("runtime", "persisted-video", 210),
            )
            store.upsertWatchLater(
                WatchLaterEntry("runtime", "later-video", 220),
            )
        }

        SQLiteLocalLibraryStore(context).use { reopened ->
            val snapshot = reopened.readSnapshot(exportedAtMs = 300)

            assertEquals(1, snapshot.watchHistory.size)
            assertEquals(1, snapshot.resumePositions.size)
            assertEquals(1, snapshot.favorites.size)
            assertEquals(1, snapshot.watchLater.size)
            assertEquals("persisted-video", snapshot.watchHistory.single().providerVideoId)
            assertEquals(12_345, snapshot.resumePositions.single().positionMs)
            assertEquals("persisted-video", snapshot.favorites.single().providerVideoId)
            assertEquals("later-video", snapshot.watchLater.single().providerVideoId)
            assertEquals(
                LocalLibrarySummary(2, 1, 1, 1, 1),
                reopened.summary(),
            )
        }
    }

    @Test
    fun schemaV1DatabaseMigratesToV2WithoutLosingProgress() {
        createV1DatabaseFixture()

        SQLiteLocalLibraryStore(context).use { migrated ->
            migrated.ensureReady()
            val snapshot = migrated.readSnapshot(exportedAtMs = 400)

            assertEquals(2, migrated.summary().schemaVersion)
            assertEquals(listOf("legacy-video"), snapshot.watchHistory.map { it.providerVideoId })
            assertEquals(listOf("legacy-video"), snapshot.resumePositions.map { it.providerVideoId })
            assertEquals(3, snapshot.watchHistory.single().playCount)
            assertEquals(4_200, snapshot.resumePositions.single().positionMs)
            assertEquals(emptyList<FavoriteEntry>(), snapshot.favorites)
            assertEquals(emptyList<WatchLaterEntry>(), snapshot.watchLater)

            migrated.upsertFavorite(FavoriteEntry("legacy", "legacy-video", 500))
            migrated.upsertWatchLater(WatchLaterEntry("legacy", "later-video", 501))
        }

        SQLiteLocalLibraryStore(context).use { reopened ->
            val snapshot = reopened.readSnapshot(exportedAtMs = 600)
            assertEquals(1, snapshot.watchHistory.size)
            assertEquals(1, snapshot.resumePositions.size)
            assertEquals(1, snapshot.favorites.size)
            assertEquals(1, snapshot.watchLater.size)
        }
    }

    @Test
    fun replaceLibraryPersistsOnlyReplacementStateAcrossReopen() {
        SQLiteLocalLibraryStore(context).use { store ->
            store.ensureReady()
            store.upsertWatchHistory(
                WatchHistoryEntry("runtime", "old-video", 10, 20, 1, false),
            )
            store.upsertResumePosition(
                ResumePositionEntry("runtime", "old-video", 1_000, 20),
            )
            store.upsertFavorite(FavoriteEntry("runtime", "old-video", 21))
            store.upsertWatchLater(WatchLaterEntry("runtime", "old-video", 22))

            store.replaceLibrary(
                LibrarySnapshotV2(
                    exportedAtMs = 500,
                    watchHistory = listOf(
                        WatchHistoryEntry("runtime", "new-video", 30, 40, 3, true),
                    ),
                    resumePositions = listOf(
                        ResumePositionEntry("runtime", "new-video", 9_000, 40),
                    ),
                    favorites = listOf(FavoriteEntry("runtime", "new-video", 41)),
                    watchLater = listOf(WatchLaterEntry("runtime", "later-video", 42)),
                ),
            )
        }

        SQLiteLocalLibraryStore(context).use { reopened ->
            val snapshot = reopened.readSnapshot(exportedAtMs = 600)
            assertEquals(listOf("new-video"), snapshot.watchHistory.map { it.providerVideoId })
            assertEquals(listOf("new-video"), snapshot.resumePositions.map { it.providerVideoId })
            assertEquals(listOf("new-video"), snapshot.favorites.map { it.providerVideoId })
            assertEquals(listOf("later-video"), snapshot.watchLater.map { it.providerVideoId })
        }
    }

    @Test
    fun legacyV1ImportReplacesProgressAndClearsV2OnlyCollections() {
        SQLiteLocalLibraryStore(context).use { store ->
            store.ensureReady()
            store.upsertFavorite(FavoriteEntry("runtime", "favorite", 10))
            store.upsertWatchLater(WatchLaterEntry("runtime", "later", 11))

            val legacyPayload = LibraryInterchangeV1Codec.encode(
                LibrarySnapshotV1(
                    exportedAtMs = 700,
                    watchHistory = listOf(
                        WatchHistoryEntry("legacy", "video", 20, 30, 1, false),
                    ),
                    resumePositions = listOf(
                        ResumePositionEntry("legacy", "video", 2_000, 30),
                    ),
                ),
            )

            val summary = LibraryPortabilityService(store).importLibrary(legacyPayload)
            assertEquals(1, summary.sourceVersion)
        }

        SQLiteLocalLibraryStore(context).use { reopened ->
            val snapshot = reopened.readSnapshot(exportedAtMs = 800)
            assertEquals(listOf("video"), snapshot.watchHistory.map { it.providerVideoId })
            assertEquals(listOf("video"), snapshot.resumePositions.map { it.providerVideoId })
            assertEquals(emptyList<FavoriteEntry>(), snapshot.favorites)
            assertEquals(emptyList<WatchLaterEntry>(), snapshot.watchLater)
        }
    }

    @Test
    fun rejectedV2ImportLeavesPersistedLibraryUnchanged() {
        SQLiteLocalLibraryStore(context).use { store ->
            store.ensureReady()
            store.upsertWatchHistory(
                WatchHistoryEntry("runtime", "existing-video", 10, 20, 1, false),
            )
            store.upsertResumePosition(
                ResumePositionEntry("runtime", "existing-video", 2_000, 20),
            )
            store.upsertFavorite(FavoriteEntry("runtime", "existing-video", 21))

            val validReplacement = LibraryInterchangeV2Codec.encode(
                LibrarySnapshotV2(
                    exportedAtMs = 700,
                    watchHistory = listOf(
                        WatchHistoryEntry("runtime", "replacement-video", 30, 40, 1, false),
                    ),
                    resumePositions = emptyList(),
                    favorites = emptyList(),
                    watchLater = emptyList(),
                ),
            )
            val tampered = validReplacement.replaceFirst("\t30\t40\t1\t0", "\t30\t41\t1\t0")

            assertThrows(LibraryInterchangeException::class.java) {
                LibraryPortabilityService(store).importLibrary(tampered)
            }
        }

        SQLiteLocalLibraryStore(context).use { reopened ->
            val snapshot = reopened.readSnapshot(exportedAtMs = 800)
            assertEquals(listOf("existing-video"), snapshot.watchHistory.map { it.providerVideoId })
            assertEquals(listOf("existing-video"), snapshot.resumePositions.map { it.providerVideoId })
            assertEquals(listOf("existing-video"), snapshot.favorites.map { it.providerVideoId })
            assertEquals(2_000, snapshot.resumePositions.single().positionMs)
        }
    }

    private fun createV1DatabaseFixture() {
        context.openOrCreateDatabase(
            SQLiteLocalLibraryStore.DATABASE_NAME,
            Context.MODE_PRIVATE,
            null,
        ).use { db ->
            val schema = context.assets.open("database/schema-v1.sql").bufferedReader().use { it.readText() }
            schema.splitToSequence(';')
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .forEach { db.execSQL(it) }

            db.execSQL(
                "INSERT INTO watch_history (provider_id, provider_video_id, first_watched_at_ms, last_watched_at_ms, play_count, completed) VALUES (?, ?, ?, ?, ?, ?)",
                arrayOf<Any?>("legacy", "legacy-video", 100, 200, 3, 0),
            )
            db.execSQL(
                "INSERT INTO resume_positions (provider_id, provider_video_id, position_ms, updated_at_ms) VALUES (?, ?, ?, ?)",
                arrayOf<Any?>("legacy", "legacy-video", 4_200, 200),
            )
            db.version = 1
        }
    }
}
