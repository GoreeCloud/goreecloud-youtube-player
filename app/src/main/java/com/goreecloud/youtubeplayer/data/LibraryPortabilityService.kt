package com.goreecloud.youtubeplayer.data

class LibraryPortabilityService(
    private val store: LocalLibraryStore,
    private val nowMs: () -> Long = System::currentTimeMillis,
) {
    fun exportProgress(): String = exportLibrary()

    fun exportLibrary(): String {
        val snapshot = store.readSnapshot(exportedAtMs = nowMs())
        return LibraryInterchangeCodec.encode(snapshot)
    }

    fun importProgress(payload: String): LibraryImportSummary = importLibrary(payload)

    fun importLibrary(payload: String): LibraryImportSummary {
        val sourceVersion = LibraryInterchangeCodec.version(payload)
        val snapshot = LibraryInterchangeCodec.decode(payload)
        store.replaceLibrary(snapshot)
        return LibraryImportSummary(
            watchHistoryCount = snapshot.watchHistory.size,
            resumePositionCount = snapshot.resumePositions.size,
            favoriteCount = snapshot.favorites.size,
            watchLaterCount = snapshot.watchLater.size,
            sourceVersion = sourceVersion,
        )
    }
}
