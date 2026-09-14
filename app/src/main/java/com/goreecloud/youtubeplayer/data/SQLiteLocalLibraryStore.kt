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

    override fun readSnapshot(exportedAtMs: Long): LibrarySnapshotV1 {
        val db = helper.readableDatabase
        return LibrarySnapshotV1(
            exportedAtMs = exportedAtMs,
            watchHistory = readWatchHistory(db),
            resumePositions = readResumePositions(db),
        )
    }

    override fun replaceProgress(snapshot: LibrarySnapshotV1) {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_WATCH_HISTORY, null, null)
            db.delete(TABLE_RESUME_POSITIONS, null, null)

            snapshot.watchHistory.forEach { entry ->
                putWatchHistory(db, entry)
            }
            snapshot.resumePositions.forEach { entry ->
                putResumePosition(db, entry)
            }

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
        val result = db.insertWithOnConflict(
            TABLE_WATCH_HISTORY,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE,
        )
        if (result == -1L) {
            throw SQLiteException("Unable to persist watch-history entry")
        }
    }

    private fun putResumePosition(db: SQLiteDatabase, entry: ResumePositionEntry) {
        val values = ContentValues().apply {
            put("provider_id", entry.providerId)
            put("provider_video_id", entry.providerVideoId)
            put("position_ms", entry.positionMs)
            put("updated_at_ms", entry.updatedAtMs)
        }
        val result = db.insertWithOnConflict(
            TABLE_RESUME_POSITIONS,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE,
        )
        if (result == -1L) {
            throw SQLiteException("Unable to persist resume-position entry")
        }
    }

    private fun readWatchHistory(db: SQLiteDatabase): List<WatchHistoryEntry> {
        return db.query(
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
    }

    private fun readResumePositions(db: SQLiteDatabase): List<ResumePositionEntry> {
        return db.query(
            TABLE_RESUME_POSITIONS,
            arrayOf(
                "provider_id",
                "provider_video_id",
                "position_ms",
                "updated_at_ms",
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
    }

    private fun countRows(db: SQLiteDatabase, table: String): Int {
        return db.rawQuery("SELECT COUNT(*) FROM $table", null).use { cursor ->
            check(cursor.moveToFirst()) { "COUNT query returned no row for $table" }
            cursor.getInt(0)
        }
    }

    private fun Cursor.string(column: String): String =
        getString(getColumnIndexOrThrow(column))

    private fun Cursor.long(column: String): Long =
        getLong(getColumnIndexOrThrow(column))

    private fun Cursor.int(column: String): Int =
        getInt(getColumnIndexOrThrow(column))

    private class DatabaseHelper(
        private val context: Context,
    ) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onConfigure(db: SQLiteDatabase) {
            super.onConfigure(db)
            db.setForeignKeyConstraintsEnabled(true)
        }

        override fun onCreate(db: SQLiteDatabase) {
            val schema = context.assets.open(SCHEMA_ASSET).bufferedReader().use { reader ->
                reader.readText()
            }
            schema.splitToSequence(';')
                .map { statement -> statement.trim() }
                .filter { statement -> statement.isNotEmpty() }
                .forEach { statement -> db.execSQL(statement) }
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            throw SQLiteException(
                "No approved local-database migration path from $oldVersion to $newVersion",
            )
        }

        override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            throw SQLiteException(
                "Database downgrade is not supported: $oldVersion to $newVersion",
            )
        }
    }

    companion object {
        const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "goreecloud-youtube-player.db"
        private const val SCHEMA_ASSET = "database/schema-v1.sql"
        private const val TABLE_WATCH_HISTORY = "watch_history"
        private const val TABLE_RESUME_POSITIONS = "resume_positions"
    }
}
