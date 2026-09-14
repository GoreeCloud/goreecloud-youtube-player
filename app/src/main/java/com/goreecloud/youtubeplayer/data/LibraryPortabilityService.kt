package com.goreecloud.youtubeplayer.data

class LibraryPortabilityService(
    private val store: LocalLibraryStore,
    private val nowMs: () -> Long = System::currentTimeMillis,
) {
    fun exportProgress(): String {
        val snapshot = store.readSnapshot(exportedAtMs = nowMs())
        return LibraryInterchangeV1Codec.encode(snapshot)
    }

    fun importProgress(payload: String): LibraryImportSummary {
        val snapshot = LibraryInterchangeV1Codec.decode(payload)
        store.replaceProgress(snapshot)
        return LibraryImportSummary(
            watchHistoryCount = snapshot.watchHistory.size,
            resumePositionCount = snapshot.resumePositions.size,
        )
    }
}
