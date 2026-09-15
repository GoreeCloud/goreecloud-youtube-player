package com.goreecloud.youtubeplayer.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class SQLiteLocalLibraryStore(
    context: Context,
) : LocalLibraryStore {
    private val helper = DatabaseHelper(context.applicationContext)

    override fun ensureReady() {
        helper.writableDatabase
    }

    override fun upsertWatchHistory(entry: WatchHistoryEntry) {
        putWatchHistory(helper.writableDatabase, entry)
    }

    override fun upsertResumePosition(entry: ResumePositionEntry) {
        putResumePosition(helper.writableDatabase, entry)
    }

    override fun upsertFavorite(entry: FavoriteEntry) {
        putFavorite(helper.writableDatabase, entry)
    }

    override fun upsertWatchLater(entry: WatchLaterEntry) {
        putWatchLater(helper.writableDatabase, entry)
    }

    override fun readSnapshot(exportedAtMs: Long): LibrarySnapshotV2 {
        val db = helper.readableDatabase
        return LibrarySnapshotV2(
            exportedAtMs = exportedAtMs,
            watchHistory = readWatchHistory(db),
            resumePositions = readResumePositions(db),
            favorites = readFavorites(db),
            watchLater = readWatchLater(db),
        )
    }

    override fun replaceLibrary(snapshot: LibrarySnapshotV2) {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_WATCH_HISTORY, null, null)
            db.delete(TABLE_RESUME_POSITIONS, null, null)
            db.delete(TABLE_FAVORITES, null, null)
            db.delete(TABLE_WATCH_LATER, null, null)

            snapshot.watchHistory.forEach { entry -> putWatchHistory(db, entry) }
            snapshot.resumePositions.forEach { entry -> putResumePosition(db, entry) }
            snapshot.favorites.forEach { entry -> putFavorite(db, entry) }
            snapshot.watchLater.forEach { entry -> putWatchLater(db, entry) }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    override fun summary(): LocalLibrarySummary {
        val db = helper.readableDatabase
        return LocalLibrarySummary(
            schemaVersion = DATABASE_VERSION,
            watchHistoryCount = countRows(db, TABLE_WATCH_HISTORY),
            resumePositionCount = countRows(db, TABLE_RESUME_POSITIONS),
            favoriteCount = countRows(db, TABLE_FAVORITES),
            watchLaterCount = countRows(db, TABLE_WATCH_LATER),
        )
    }

    override fun close() {
        helper.close()
    }

    private fun putWatchHistory(db: SQLiteDatabase, entry: WatchHistoryEntry) {
        val values = ContentValues().apply {
            put("provider_id", entry.providerId)
            put("provider_video_id", entry.providerVideoId)
            put("first_watched_at_ms", entry.firstWatchedAtMs)
            put("last_watched_at_ms", entry.lastWatchedAtMs)
            put("play_count", entry.playCount)
            put("completed", if (entry.completed) 1 else 0)
        }
        insertOrThrow(db, TABLE_WATCH_HISTORY, values, "watch-history")
    }

    private fun putResumePosition(db: SQLiteDatabase, entry: ResumePositionEntry) {
        val values = ContentValues().apply {
            put("provider_id", entry.providerId)
            put("provider_video_id", entry.providerVideoId)
            put("position_ms", entry.positionMs)
            put("updated_at_ms", entry.updatedAtMs)
        }
        insertOrThrow(db, TABLE_RESUME_POSITIONS, values, "resume-position")
    }

    private fun putFavorite(db: SQLiteDatabase, entry: FavoriteEntry) {
        val values = ContentValues().apply {
            put("provider_id", entry.providerId)
            put("provider_video_id", entry.providerVideoId)
            put("added_at_ms", entry.addedAtMs)
        }
        insertOrThrow(db, TABLE_FAVORITES, values, "favorite")
    }

    private fun putWatchLater(db: SQLiteDatabase, entry: WatchLaterEntry) {
        val values = ContentValues().apply {
            put("provider_id", entry.providerId)
            put("provider_video_id", entry.providerVideoId)
            put("added_at_ms", entry.addedAtMs)
        }
        insertOrThrow(db, TABLE_WATCH_LATER, values, "watch-later")
    }

    private fun insertOrThrow(
        db: SQLiteDatabase,
        table: String,
        values: ContentValues,
        label: String,
    ) {
        val result = db.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        if (result == -1L) {
            throw SQLiteException("Unable to persist $label entry")
        }
    }

    private fun readWatchHistory(db: SQLiteDatabase): List<WatchHistoryEntry> =
        db.query(
            TABLE_WATCH_HISTORY,
            arrayOf(
                "provider_id",
                "provider_video_id",
                "first_watched_at_ms",
                "last_watched_at_ms",
                "play_count",
                "completed",
            ),
            null,
            null,
            null,
            null,
            "provider_id ASC, provider_video_id ASC",
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(
                        WatchHistoryEntry(
                            providerId = cursor.string("provider_id"),
                            providerVideoId = cursor.string("provider_video_id"),
                            firstWatchedAtMs = cursor.long("first_watched_at_ms"),
                            lastWatchedAtMs = cursor.long("last_watched_at_ms"),
                            playCount = cursor.int("play_count"),
                            completed = cursor.int("completed") == 1,
                        ),
                    )
                }
            }
        }

    private fun readResumePositions(db: SQLiteDatabase): List<ResumePositionEntry> =
        db.query(
            TABLE_RESUME_POSITIONS,
            arrayOf("provider_id", "provider_video_id", "position_ms", "updated_at_ms"),
            null,
            null,
            null,
            null,
            "provider_id ASC, provider_video_id ASC",
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(
                        ResumePositionEntry(
                            providerId = cursor.string("provider_id"),
                            providerVideoId = cursor.string("provider_video_id"),
                            positionMs = cursor.long("position_ms"),
                            updatedAtMs = cursor.long("updated_at_ms"),
                        ),
                    )
                }
            }
        }

    private fun readFavorites(db: SQLiteDatabase): List<FavoriteEntry> =
        db.query(
            TABLE_FAVORITES,
            arrayOf("provider_id", "provider_video_id", "added_at_ms"),
            null,
            null,
            null,
            null,
            "provider_id ASC, provider_video_id ASC",
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(
                        FavoriteEntry(
                            providerId = cursor.string("provider_id"),
                            providerVideoId = cursor.string("provider_video_id"),
                            addedAtMs = cursor.long("added_at_ms"),
                        ),
                    )
                }
            }
        }

    private fun readWatchLater(db: SQLiteDatabase): List<WatchLaterEntry> =
        db.query(
            TABLE_WATCH_LATER,
            arrayOf("provider_id", "provider_video_id", "added_at_ms"),
            null,
            null,
            null,
            null,
            "provider_id ASC, provider_video_id ASC",
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(
                        WatchLaterEntry(
                            providerId = cursor.string("provider_id"),
                            providerVideoId = cursor.string("provider_video_id"),
                            addedAtMs = cursor.long("added_at_ms"),
                        ),
                    )
                }
            }
        }

    private fun countRows(db: SQLiteDatabase, table: String): Int =
        db.rawQuery("SELECT COUNT(*) FROM $table", null).use { cursor ->
            check(cursor.moveToFirst()) { "COUNT query returned no row for $table" }
            cursor.getInt(0)
        }

    private fun Cursor.string(column: String): String = getString(getColumnIndexOrThrow(column))
    private fun Cursor.long(column: String): Long = getLong(getColumnIndexOrThrow(column))
    private fun Cursor.int(column: String): Int = getInt(getColumnIndexOrThrow(column))

    private class DatabaseHelper(
        private val context: Context,
    ) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onConfigure(db: SQLiteDatabase) {
            super.onConfigure(db)
            db.setForeignKeyConstraintsEnabled(true)
        }

        override fun onCreate(db: SQLiteDatabase) {
            executeAssetSql(db, SCHEMA_ASSET)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            if (oldVersion == 1 && newVersion == 2) {
                executeAssetSql(db, MIGRATION_V1_TO_V2_ASSET)
                return
            }
            throw SQLiteException(
                "No approved local-database migration path from $oldVersion to $newVersion",
            )
        }

        override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            throw SQLiteException(
                "Database downgrade is not supported: $oldVersion to $newVersion",
            )
        }

        private fun executeAssetSql(db: SQLiteDatabase, assetPath: String) {
            val sql = context.assets.open(assetPath).bufferedReader().use { reader -> reader.readText() }
            sql.splitToSequence(';')
                .map { statement -> statement.trim() }
                .filter { statement -> statement.isNotEmpty() }
                .forEach { statement -> db.execSQL(statement) }
        }
    }

    companion object {
        const val DATABASE_VERSION = 2
        internal const val DATABASE_NAME = "goreecloud-youtube-player.db"
        private const val SCHEMA_ASSET = "database/schema-v2.sql"
        private const val MIGRATION_V1_TO_V2_ASSET = "database/migration-v1-to-v2.sql"
        private const val TABLE_WATCH_HISTORY = "watch_history"
        private const val TABLE_RESUME_POSITIONS = "resume_positions"
        private const val TABLE_FAVORITES = "favorites"
        private const val TABLE_WATCH_LATER = "watch_later"
    }
}
