package com.goreecloud.youtubeplayer.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class LibraryInterchangeV2CodecTest {
    @Test
    fun roundTrip_preservesAllV2RecordClassesDeterministically() {
        val snapshot = LibrarySnapshotV2(
            exportedAtMs = 1_234,
            watchHistory = listOf(
                WatchHistoryEntry("youtube", "video-2", 20, 30, 2, true),
                WatchHistoryEntry("youtube", "video-1", 10, 15, 1, false),
            ),
            resumePositions = listOf(
                ResumePositionEntry("youtube", "video-1", 9_000, 15),
            ),
            favorites = listOf(
                FavoriteEntry("youtube", "favorite-1", 40),
            ),
            watchLater = listOf(
                WatchLaterEntry("youtube", "later-1", 50),
            ),
        )

        val payload = LibraryInterchangeV2Codec.encode(snapshot)
        val decoded = LibraryInterchangeV2Codec.decode(payload)

        assertEquals(2, LibraryInterchangeCodec.version(payload))
        assertEquals(snapshot.exportedAtMs, decoded.exportedAtMs)
        assertEquals(listOf("video-1", "video-2"), decoded.watchHistory.map { it.providerVideoId })
        assertEquals(snapshot.resumePositions, decoded.resumePositions)
        assertEquals(snapshot.favorites, decoded.favorites)
        assertEquals(snapshot.watchLater, decoded.watchLater)
        assertEquals(payload, LibraryInterchangeV2Codec.encode(decoded))
    }

    @Test
    fun dispatcher_upgradesV1PayloadToV2WithEmptyNewCollections() {
        val v1 = LibrarySnapshotV1(
            exportedAtMs = 99,
            watchHistory = listOf(
                WatchHistoryEntry("legacy", "video", 1, 2, 1, false),
            ),
            resumePositions = listOf(
                ResumePositionEntry("legacy", "video", 500, 2),
            ),
        )
        val payload = LibraryInterchangeV1Codec.encode(v1)

        val decoded = LibraryInterchangeCodec.decode(payload)

        assertEquals(1, LibraryInterchangeCodec.version(payload))
        assertEquals(v1.watchHistory, decoded.watchHistory)
        assertEquals(v1.resumePositions, decoded.resumePositions)
        assertEquals(emptyList<FavoriteEntry>(), decoded.favorites)
        assertEquals(emptyList<WatchLaterEntry>(), decoded.watchLater)
    }

    @Test
    fun tamperedV2PayloadFailsIntegrityValidation() {
        val payload = LibraryInterchangeV2Codec.encode(
            LibrarySnapshotV2(
                exportedAtMs = 100,
                watchHistory = emptyList(),
                resumePositions = emptyList(),
                favorites = listOf(FavoriteEntry("youtube", "video", 50)),
                watchLater = emptyList(),
            ),
        )
        val tampered = payload.replace("\t50\n", "\t51\n")

        assertThrows(LibraryInterchangeException::class.java) {
            LibraryInterchangeV2Codec.decode(tampered)
        }
    }

    @Test
    fun duplicateFavoriteIdentityIsRejectedBeforeEncoding() {
        assertThrows(IllegalArgumentException::class.java) {
            LibrarySnapshotV2(
                exportedAtMs = 1,
                watchHistory = emptyList(),
                resumePositions = emptyList(),
                favorites = listOf(
                    FavoriteEntry("youtube", "same", 1),
                    FavoriteEntry("youtube", "same", 2),
                ),
                watchLater = emptyList(),
            )
        }
    }
}
