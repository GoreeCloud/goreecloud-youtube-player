package com.goreecloud.youtubeplayer.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class LibraryInterchangeV1CodecTest {
    @Test
    fun roundTrip_preservesValidatedProgressAndUsesDeterministicOrdering() {
        val snapshot = LibrarySnapshotV1(
            exportedAtMs = 1_726_330_000_000,
            watchHistory = listOf(
                WatchHistoryEntry("youtube", "video-b", 10, 20, 2, false),
                WatchHistoryEntry("local", "video-a", 30, 30, 1, true),
            ),
            resumePositions = listOf(
                ResumePositionEntry("youtube", "video-b", 4_000, 50),
                ResumePositionEntry("local", "video-a", 7_000, 60),
            ),
        )

        val encoded = LibraryInterchangeV1Codec.encode(snapshot)
        val decoded = LibraryInterchangeV1Codec.decode(encoded)

        assertEquals(snapshot.exportedAtMs, decoded.exportedAtMs)
        assertEquals(listOf("local", "youtube"), decoded.watchHistory.map { it.providerId })
        assertEquals(listOf("local", "youtube"), decoded.resumePositions.map { it.providerId })
        assertTrue(encoded.startsWith("GCYTP-LIBRARY\t1\t"))
        assertTrue(encoded.contains("\nEND\t2\t2\t"))
    }

    @Test
    fun decode_rejectsPayloadWhoseIntegrityDigestNoLongerMatches() {
        val encoded = LibraryInterchangeV1Codec.encode(
            LibrarySnapshotV1(
                exportedAtMs = 100,
                watchHistory = listOf(
                    WatchHistoryEntry("youtube", "video-a", 10, 20, 1, false),
                ),
                resumePositions = emptyList(),
            ),
        )
        val tampered = encoded.replace("\t10\t20\t1\t0", "\t10\t21\t1\t0")

        val error = assertThrows(LibraryInterchangeException::class.java) {
            LibraryInterchangeV1Codec.decode(tampered)
        }

        assertTrue(error.message.orEmpty().contains("integrity", ignoreCase = true))
    }

    @Test
    fun snapshot_rejectsDuplicateProviderVideoIdentity() {
        assertThrows(IllegalArgumentException::class.java) {
            LibrarySnapshotV1(
                exportedAtMs = 100,
                watchHistory = listOf(
                    WatchHistoryEntry("youtube", "same", 10, 20, 1, false),
                    WatchHistoryEntry("youtube", "same", 20, 30, 2, true),
                ),
                resumePositions = emptyList(),
            )
        }
    }
}
