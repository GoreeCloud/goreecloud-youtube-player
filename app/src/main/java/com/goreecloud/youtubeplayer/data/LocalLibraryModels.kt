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
        requireIdentity(providerId, providerVideoId)
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
        requireIdentity(providerId, providerVideoId)
        require(positionMs >= 0) { "positionMs must be non-negative" }
        require(updatedAtMs >= 0) { "updatedAtMs must be non-negative" }
    }
}

data class FavoriteEntry(
    val providerId: String,
    val providerVideoId: String,
    val addedAtMs: Long,
) {
    init {
        requireIdentity(providerId, providerVideoId)
        require(addedAtMs >= 0) { "addedAtMs must be non-negative" }
    }
}

data class WatchLaterEntry(
    val providerId: String,
    val providerVideoId: String,
    val addedAtMs: Long,
) {
    init {
        requireIdentity(providerId, providerVideoId)
        require(addedAtMs >= 0) { "addedAtMs must be non-negative" }
    }
}

data class LibrarySnapshotV1(
    val exportedAtMs: Long,
    val watchHistory: List<WatchHistoryEntry>,
    val resumePositions: List<ResumePositionEntry>,
) {
    init {
        require(exportedAtMs >= 0) { "exportedAtMs must be non-negative" }
        requireUniqueIdentities(watchHistory.map { it.providerId to it.providerVideoId }, "watch-history")
        requireUniqueIdentities(resumePositions.map { it.providerId to it.providerVideoId }, "resume-position")
    }
}

data class LibrarySnapshotV2(
    val exportedAtMs: Long,
    val watchHistory: List<WatchHistoryEntry>,
    val resumePositions: List<ResumePositionEntry>,
    val favorites: List<FavoriteEntry>,
    val watchLater: List<WatchLaterEntry>,
) {
    init {
        require(exportedAtMs >= 0) { "exportedAtMs must be non-negative" }
        requireUniqueIdentities(watchHistory.map { it.providerId to it.providerVideoId }, "watch-history")
        requireUniqueIdentities(resumePositions.map { it.providerId to it.providerVideoId }, "resume-position")
        requireUniqueIdentities(favorites.map { it.providerId to it.providerVideoId }, "favorite")
        requireUniqueIdentities(watchLater.map { it.providerId to it.providerVideoId }, "watch-later")
    }
}

fun LibrarySnapshotV1.toV2(): LibrarySnapshotV2 =
    LibrarySnapshotV2(
        exportedAtMs = exportedAtMs,
        watchHistory = watchHistory,
        resumePositions = resumePositions,
        favorites = emptyList(),
        watchLater = emptyList(),
    )

data class LocalLibrarySummary(
    val schemaVersion: Int,
    val watchHistoryCount: Int,
    val resumePositionCount: Int,
    val favoriteCount: Int = 0,
    val watchLaterCount: Int = 0,
)

data class LibraryImportSummary(
    val watchHistoryCount: Int,
    val resumePositionCount: Int,
    val favoriteCount: Int = 0,
    val watchLaterCount: Int = 0,
    val sourceVersion: Int = 1,
)

private fun requireIdentity(providerId: String, providerVideoId: String) {
    require(providerId.isNotBlank()) { "providerId must not be blank" }
    require(providerVideoId.isNotBlank()) { "providerVideoId must not be blank" }
}

private fun requireUniqueIdentities(
    identities: List<Pair<String, String>>,
    label: String,
) {
    val keys = HashSet<Pair<String, String>>()
    identities.forEach { identity ->
        require(keys.add(identity)) {
            "Duplicate $label identity: ${identity.first}/${identity.second}"
        }
    }
}
