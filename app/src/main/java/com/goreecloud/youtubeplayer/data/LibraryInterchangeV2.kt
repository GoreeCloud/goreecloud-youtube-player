package com.goreecloud.youtubeplayer.data

import java.nio.charset.StandardCharsets.UTF_8
import java.security.MessageDigest
import java.util.Base64

object LibraryInterchangeCodec {
    private const val FORMAT = "GCYTP-LIBRARY"

    fun encode(snapshot: LibrarySnapshotV2): String = LibraryInterchangeV2Codec.encode(snapshot)

    fun decode(payload: String): LibrarySnapshotV2 = when (version(payload)) {
        1 -> LibraryInterchangeV1Codec.decode(payload).toV2()
        2 -> LibraryInterchangeV2Codec.decode(payload)
        else -> throw LibraryInterchangeException("Unsupported interchange version")
    }

    fun version(payload: String): Int {
        if (payload.isEmpty()) {
            throw LibraryInterchangeException("Library interchange payload is empty")
        }
        val firstLine = payload.lineSequence().firstOrNull()?.trimEnd('\r')
            ?: throw LibraryInterchangeException("Library interchange payload is incomplete")
        val fields = firstLine.split('\t')
        if (fields.size != 3 || fields[0] != FORMAT) {
            throw LibraryInterchangeException("Missing or malformed interchange header")
        }
        return fields[1].toIntOrNull()
            ?: throw LibraryInterchangeException("Interchange version is not numeric")
    }
}

object LibraryInterchangeV2Codec {
    private const val FORMAT = "GCYTP-LIBRARY"
    private const val VERSION = 2
    private const val END = "END"
    private const val WATCH = "W"
    private const val RESUME = "R"
    private const val FAVORITE = "F"
    private const val WATCH_LATER = "L"

    fun encode(snapshot: LibrarySnapshotV2): String {
        val watchEntries = snapshot.watchHistory.sortedWith(
            compareBy(WatchHistoryEntry::providerId, WatchHistoryEntry::providerVideoId),
        )
        val resumeEntries = snapshot.resumePositions.sortedWith(
            compareBy(ResumePositionEntry::providerId, ResumePositionEntry::providerVideoId),
        )
        val favoriteEntries = snapshot.favorites.sortedWith(
            compareBy(FavoriteEntry::providerId, FavoriteEntry::providerVideoId),
        )
        val watchLaterEntries = snapshot.watchLater.sortedWith(
            compareBy(WatchLaterEntry::providerId, WatchLaterEntry::providerVideoId),
        )

        val bodyLines = buildList {
            add("$FORMAT\t$VERSION\t${snapshot.exportedAtMs}")
            watchEntries.forEach { entry ->
                add(
                    listOf(
                        WATCH,
                        encodeToken(entry.providerId),
                        encodeToken(entry.providerVideoId),
                        entry.firstWatchedAtMs.toString(),
                        entry.lastWatchedAtMs.toString(),
                        entry.playCount.toString(),
                        if (entry.completed) "1" else "0",
                    ).joinToString("\t"),
                )
            }
            resumeEntries.forEach { entry ->
                add(
                    listOf(
                        RESUME,
                        encodeToken(entry.providerId),
                        encodeToken(entry.providerVideoId),
                        entry.positionMs.toString(),
                        entry.updatedAtMs.toString(),
                    ).joinToString("\t"),
                )
            }
            favoriteEntries.forEach { entry ->
                add(
                    listOf(
                        FAVORITE,
                        encodeToken(entry.providerId),
                        encodeToken(entry.providerVideoId),
                        entry.addedAtMs.toString(),
                    ).joinToString("\t"),
                )
            }
            watchLaterEntries.forEach { entry ->
                add(
                    listOf(
                        WATCH_LATER,
                        encodeToken(entry.providerId),
                        encodeToken(entry.providerVideoId),
                        entry.addedAtMs.toString(),
                    ).joinToString("\t"),
                )
            }
        }

        val body = bodyLines.joinToString(separator = "\n", postfix = "\n")
        val digest = sha256(body)
        val footer = listOf(
            END,
            watchEntries.size.toString(),
            resumeEntries.size.toString(),
            favoriteEntries.size.toString(),
            watchLaterEntries.size.toString(),
            digest,
        ).joinToString("\t") + "\n"
        return body + footer
    }

    fun decode(payload: String): LibrarySnapshotV2 {
        val lines = normalizedLines(payload)
        if (lines.size < 2) {
            throw LibraryInterchangeException("Library interchange payload is incomplete")
        }
        if (lines.any { it.isEmpty() }) {
            throw LibraryInterchangeException("Blank lines are not allowed in the interchange payload")
        }

        val footerFields = lines.last().split('\t')
        if (footerFields.size != 6 || footerFields[0] != END) {
            throw LibraryInterchangeException("Missing or malformed interchange footer")
        }

        val bodyLines = lines.dropLast(1)
        val body = bodyLines.joinToString(separator = "\n", postfix = "\n")
        verifyDigest(body, footerFields[5])

        val expectedWatchCount = footerFields[1].nonNegativeInt("watch-history count")
        val expectedResumeCount = footerFields[2].nonNegativeInt("resume-position count")
        val expectedFavoriteCount = footerFields[3].nonNegativeInt("favorite count")
        val expectedWatchLaterCount = footerFields[4].nonNegativeInt("watch-later count")

        val headerFields = bodyLines.first().split('\t')
        if (headerFields.size != 3 || headerFields[0] != FORMAT) {
            throw LibraryInterchangeException("Missing or malformed interchange header")
        }
        val version = headerFields[1].toIntOrNull()
            ?: throw LibraryInterchangeException("Interchange version is not numeric")
        if (version != VERSION) {
            throw LibraryInterchangeException("Unsupported interchange version: $version")
        }
        val exportedAtMs = headerFields[2].nonNegativeLong("export timestamp")

        val watchHistory = mutableListOf<WatchHistoryEntry>()
        val resumePositions = mutableListOf<ResumePositionEntry>()
        val favorites = mutableListOf<FavoriteEntry>()
        val watchLater = mutableListOf<WatchLaterEntry>()

        bodyLines.drop(1).forEachIndexed { index, line ->
            val lineNumber = index + 2
            val fields = line.split('\t')
            when (fields.firstOrNull()) {
                WATCH -> watchHistory += parseWatch(fields, lineNumber)
                RESUME -> resumePositions += parseResume(fields, lineNumber)
                FAVORITE -> favorites += parseFavorite(fields, lineNumber)
                WATCH_LATER -> watchLater += parseWatchLater(fields, lineNumber)
                else -> throw LibraryInterchangeException(
                    "Unsupported record type on line $lineNumber",
                )
            }
        }

        if (
            watchHistory.size != expectedWatchCount ||
            resumePositions.size != expectedResumeCount ||
            favorites.size != expectedFavoriteCount ||
            watchLater.size != expectedWatchLaterCount
        ) {
            throw LibraryInterchangeException("Interchange footer counts do not match the payload")
        }

        return try {
            LibrarySnapshotV2(
                exportedAtMs = exportedAtMs,
                watchHistory = watchHistory,
                resumePositions = resumePositions,
                favorites = favorites,
                watchLater = watchLater,
            )
        } catch (error: IllegalArgumentException) {
            throw LibraryInterchangeException(error.message ?: "Invalid interchange payload")
        }
    }

    private fun normalizedLines(payload: String): List<String> {
        if (payload.isEmpty()) {
            throw LibraryInterchangeException("Library interchange payload is empty")
        }
        return payload.split('\n')
            .map { line -> line.trimEnd('\r') }
            .let { lines -> if (lines.lastOrNull().isNullOrEmpty()) lines.dropLast(1) else lines }
    }

    private fun verifyDigest(body: String, expectedDigest: String) {
        val actualDigest = sha256(body)
        if (!MessageDigest.isEqual(expectedDigest.toByteArray(UTF_8), actualDigest.toByteArray(UTF_8))) {
            throw LibraryInterchangeException("Interchange integrity verification failed")
        }
    }

    private fun parseWatch(fields: List<String>, lineNumber: Int): WatchHistoryEntry {
        if (fields.size != 7) {
            throw LibraryInterchangeException("Malformed watch-history record on line $lineNumber")
        }
        val completed = when (fields[6]) {
            "0" -> false
            "1" -> true
            else -> throw LibraryInterchangeException("Invalid completion flag on line $lineNumber")
        }
        return try {
            WatchHistoryEntry(
                providerId = decodeToken(fields[1], lineNumber),
                providerVideoId = decodeToken(fields[2], lineNumber),
                firstWatchedAtMs = fields[3].nonNegativeLong("first watched timestamp", lineNumber),
                lastWatchedAtMs = fields[4].nonNegativeLong("last watched timestamp", lineNumber),
                playCount = fields[5].positiveInt("play count", lineNumber),
                completed = completed,
            )
        } catch (error: IllegalArgumentException) {
            if (error is LibraryInterchangeException) throw error
            throw LibraryInterchangeException(error.message ?: "Invalid watch-history record")
        }
    }

    private fun parseResume(fields: List<String>, lineNumber: Int): ResumePositionEntry {
        if (fields.size != 5) {
            throw LibraryInterchangeException("Malformed resume-position record on line $lineNumber")
        }
        return try {
            ResumePositionEntry(
                providerId = decodeToken(fields[1], lineNumber),
                providerVideoId = decodeToken(fields[2], lineNumber),
                positionMs = fields[3].nonNegativeLong("resume position", lineNumber),
                updatedAtMs = fields[4].nonNegativeLong("resume updated timestamp", lineNumber),
            )
        } catch (error: IllegalArgumentException) {
            if (error is LibraryInterchangeException) throw error
            throw LibraryInterchangeException(error.message ?: "Invalid resume-position record")
        }
    }

    private fun parseFavorite(fields: List<String>, lineNumber: Int): FavoriteEntry {
        if (fields.size != 4) {
            throw LibraryInterchangeException("Malformed favorite record on line $lineNumber")
        }
        return try {
            FavoriteEntry(
                providerId = decodeToken(fields[1], lineNumber),
                providerVideoId = decodeToken(fields[2], lineNumber),
                addedAtMs = fields[3].nonNegativeLong("favorite added timestamp", lineNumber),
            )
        } catch (error: IllegalArgumentException) {
            if (error is LibraryInterchangeException) throw error
            throw LibraryInterchangeException(error.message ?: "Invalid favorite record")
        }
    }

    private fun parseWatchLater(fields: List<String>, lineNumber: Int): WatchLaterEntry {
        if (fields.size != 4) {
            throw LibraryInterchangeException("Malformed watch-later record on line $lineNumber")
        }
        return try {
            WatchLaterEntry(
                providerId = decodeToken(fields[1], lineNumber),
                providerVideoId = decodeToken(fields[2], lineNumber),
                addedAtMs = fields[3].nonNegativeLong("watch-later added timestamp", lineNumber),
            )
        } catch (error: IllegalArgumentException) {
            if (error is LibraryInterchangeException) throw error
            throw LibraryInterchangeException(error.message ?: "Invalid watch-later record")
        }
    }

    private fun encodeToken(value: String): String =
        Base64.getUrlEncoder().withoutPadding().encodeToString(value.toByteArray(UTF_8))

    private fun decodeToken(value: String, lineNumber: Int): String = try {
        val decoded = String(Base64.getUrlDecoder().decode(value), UTF_8)
        if (decoded.isBlank()) {
            throw LibraryInterchangeException("Blank identity token on line $lineNumber")
        }
        decoded
    } catch (error: IllegalArgumentException) {
        if (error is LibraryInterchangeException) throw error
        throw LibraryInterchangeException("Invalid identity token on line $lineNumber")
    }

    private fun sha256(value: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray(UTF_8))
            .joinToString("") { byte -> "%02x".format(byte.toInt() and 0xff) }

    private fun String.nonNegativeLong(name: String, lineNumber: Int? = null): Long {
        val parsed = toLongOrNull()
            ?: throw LibraryInterchangeException("$name is not numeric${lineSuffix(lineNumber)}")
        if (parsed < 0) {
            throw LibraryInterchangeException("$name must be non-negative${lineSuffix(lineNumber)}")
        }
        return parsed
    }

    private fun String.nonNegativeInt(name: String): Int {
        val parsed = toIntOrNull()
            ?: throw LibraryInterchangeException("$name is not numeric")
        if (parsed < 0) {
            throw LibraryInterchangeException("$name must be non-negative")
        }
        return parsed
    }

    private fun String.positiveInt(name: String, lineNumber: Int): Int {
        val parsed = toIntOrNull()
            ?: throw LibraryInterchangeException("$name is not numeric${lineSuffix(lineNumber)}")
        if (parsed <= 0) {
            throw LibraryInterchangeException("$name must be positive${lineSuffix(lineNumber)}")
        }
        return parsed
    }

    private fun lineSuffix(lineNumber: Int?): String = lineNumber?.let { " on line $it" }.orEmpty()
}
