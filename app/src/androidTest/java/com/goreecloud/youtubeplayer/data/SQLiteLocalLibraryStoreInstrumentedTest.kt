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
        context.deleteDatabase(DATABASE_NAME)
    }

    @After
    fun tearDown() {
        context.deleteDatabase(DATABASE_NAME)
    }

    @Test
    fun schemaInitializesAndProgressPersistsAcrossReopen() {
        SQLiteLocalLibraryStore(context).use { store ->
            store.ensureReady()
            assertEquals(
                LocalLibrarySummary(
                    schemaVersion = 1,
                    watchHistoryCount = 0,
                    resumePositionCount = 0,
                ),
                store.summary(),
            )

            store.upsertWatchHistory(
                WatchHistoryEntry(
                    providerId = "runtime",
                    providerVideoId = "persisted-video",
                    firstWatchedAtMs = 100,
                    lastWatchedAtMs = 200,
                    playCount = 2,
                    completed = false,
                ),
            )
            store.upsertResumePosition(
                ResumePositionEntry(
                    providerId = "runtime",
                    providerVideoId = "persisted-video",
                    positionMs = 12_345,
                    updatedAtMs = 200,
                ),
            )
        }

        SQLiteLocalLibraryStore(context).use { reopened ->
            reopened.ensureReady()
            val snapshot = reopened.readSnapshot(exportedAtMs = 300)

            assertEquals(1, snapshot.watchHistory.size)
            assertEquals(1, snapshot.resumePositions.size)
            assertEquals("persisted-video", snapshot.watchHistory.single().providerVideoId)
            assertEquals(2, snapshot.watchHistory.single().playCount)
            assertEquals(12_345, snapshot.resumePositions.single().positionMs)
            assertEquals(
                LocalLibrarySummary(
                    schemaVersion = 1,
                    watchHistoryCount = 1,
                    resumePositionCount = 1,
                ),
                reopened.summary(),
            )
        }
    }

    @Test
    fun replaceProgressPersistsOnlyReplacementStateAcrossReopen() {
        SQLiteLocalLibraryStore(context).use { store ->
            store.ensureReady()
            store.upsertWatchHistory(
                WatchHistoryEntry("runtime", "old-video", 10, 20, 1, false),
            )
            store.upsertResumePosition(
                ResumePositionEntry("runtime", "old-video", 1_000, 20),
            )

            store.replaceProgress(
                LibrarySnapshotV1(
                    exportedAtMs = 500,
                    watchHistory = listOf(
                        WatchHistoryEntry("runtime", "new-video", 30, 40, 3, true),
                    ),
                    resumePositions = listOf(
                        ResumePositionEntry("runtime", "new-video", 9_000, 40),
                    ),
                ),
            )
        }

        SQLiteLocalLibraryStore(context).use { reopened ->
            val snapshot = reopened.readSnapshot(exportedAtMs = 600)
            assertEquals(listOf("new-video"), snapshot.watchHistory.map { it.providerVideoId })
            assertEquals(listOf("new-video"), snapshot.resumePositions.map { it.providerVideoId })
            assertEquals(true, snapshot.watchHistory.single().completed)
            assertEquals(9_000, snapshot.resumePositions.single().positionMs)
        }
    }

    @Test
    fun rejectedImportLeavesPersistedProgressUnchanged() {
        SQLiteLocalLibraryStore(context).use { store ->
            store.ensureReady()
            store.upsertWatchHistory(
                WatchHistoryEntry("runtime", "existing-video", 10, 20, 1, false),
            )
            store.upsertResumePosition(
                ResumePositionEntry("runtime", "existing-video", 2_000, 20),
            )

            val validReplacement = LibraryInterchangeV1Codec.encode(
                LibrarySnapshotV1(
                    exportedAtMs = 700,
                    watchHistory = listOf(
                        WatchHistoryEntry("runtime", "replacement-video", 30, 40, 1, false),
                    ),
                    resumePositions = emptyList(),
                ),
            )
            val tampered = validReplacement.replaceFirst("\t30\t40\t1\t0", "\t30\t41\t1\t0")

            assertThrows(LibraryInterchangeException::class.java) {
                LibraryPortabilityService(store).importProgress(tampered)
            }
        }

        SQLiteLocalLibraryStore(context).use { reopened ->
            val snapshot = reopened.readSnapshot(exportedAtMs = 800)
            assertEquals(listOf("existing-video"), snapshot.watchHistory.map { it.providerVideoId })
            assertEquals(listOf("existing-video"), snapshot.resumePositions.map { it.providerVideoId })
            assertEquals(2_000, snapshot.resumePositions.single().positionMs)
        }
    }

    private companion object {
        const val DATABASE_NAME = "goreecloud-youtube-player.db"
    }
}
