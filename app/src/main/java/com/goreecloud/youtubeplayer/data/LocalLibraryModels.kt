package com.goreecloud.youtubeplayer.data

data class WatchHistoryEntry(
    val providerId: String,
    val providerVideoId: String,
    val firstWatchedAtMs: Long,
    val lastWatchedAtMs: Long,
    val playCount: Int,
    val completed: Boolean,
) {
    init {
        require(providerId.isNotBlank()) { "providerId must not be blank" }
        require(providerVideoId.isNotBlank()) { "providerVideoId must not be blank" }
        require(firstWatchedAtMs >= 0) { "firstWatchedAtMs must be non-negative" }
        require(lastWatchedAtMs >= firstWatchedAtMs) {
            "lastWatchedAtMs must be greater than or equal to firstWatchedAtMs"
        }
        require(playCount > 0) { "playCount must be positive" }
    }
}

data class ResumePositionEntry(
    val providerId: String,
    val providerVideoId: String,
    val positionMs: Long,
    val updatedAtMs: Long,
) {
    init {
        require(providerId.isNotBlank()) { "providerId must not be blank" }
        require(providerVideoId.isNotBlank()) { "providerVideoId must not be blank" }
        require(positionMs >= 0) { "positionMs must be non-negative" }
        require(updatedAtMs >= 0) { "updatedAtMs must be non-negative" }
    }
}

data class LibrarySnapshotV1(
    val exportedAtMs: Long,
    val watchHistory: List<WatchHistoryEntry>,
    val resumePositions: List<ResumePositionEntry>,
) {
    init {
        require(exportedAtMs >= 0) { "exportedAtMs must be non-negative" }
        requireUniqueWatchHistory(watchHistory)
        requireUniqueResumePositions(resumePositions)
    }

    private fun requireUniqueWatchHistory(entries: List<WatchHistoryEntry>) {
        val keys = HashSet<Pair<String, String>>()
        entries.forEach { entry ->
            require(keys.add(entry.providerId to entry.providerVideoId)) {
                "Duplicate watch-history identity: ${entry.providerId}/${entry.providerVideoId}"
            }
        }
    }

    private fun requireUniqueResumePositions(entries: List<ResumePositionEntry>) {
        val keys = HashSet<Pair<String, String>>()
        entries.forEach { entry ->
            require(keys.add(entry.providerId to entry.providerVideoId)) {
                "Duplicate resume-position identity: ${entry.providerId}/${entry.providerVideoId}"
            }
        }
    }
}

data class LocalLibrarySummary(
    val schemaVersion: Int,
    val watchHistoryCount: Int,
    val resumePositionCount: Int,
)

data class LibraryImportSummary(
    val watchHistoryCount: Int,
    val resumePositionCount: Int,
)
