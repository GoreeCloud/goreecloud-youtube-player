package com.goreecloud.youtubeplayer.data

import java.nio.charset.StandardCharsets.UTF_8
import java.security.MessageDigest
import java.util.Base64

class LibraryInterchangeException(message: String) : IllegalArgumentException(message)

object LibraryInterchangeV1Codec {
    private const val FORMAT = "GCYTP-LIBRARY"
    private const val VERSION = 1
    private const val END = "END"
    private const val WATCH = "W"
    private const val RESUME = "R"

    fun encode(snapshot: LibrarySnapshotV1): String {
        val watchEntries = snapshot.watchHistory.sortedWith(
            compareBy(WatchHistoryEntry::providerId, WatchHistoryEntry::providerVideoId),
        )
        val resumeEntries = snapshot.resumePositions.sortedWith(
            compareBy(ResumePositionEntry::providerId, ResumePositionEntry::providerVideoId),
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
        }

        val body = bodyLines.joinToString(separator = "\n", postfix = "\n")
        val digest = sha256(body)
        val footer = "$END\t${watchEntries.size}\t${resumeEntries.size}\t$digest\n"
        return body + footer
    }

    fun decode(payload: String): LibrarySnapshotV1 {
        if (payload.isEmpty()) {
            throw LibraryInterchangeException("Library interchange payload is empty")
        }

        val normalizedLines = payload.split('\n')
            .map { line -> line.trimEnd('\r') }
            .let { lines -> if (lines.lastOrNull().isNullOrEmpty()) lines.dropLast(1) else lines }

        if (normalizedLines.size < 2) {
            throw LibraryInterchangeException("Library interchange payload is incomplete")
        }
        if (normalizedLines.any { line -> line.isEmpty() }) {
            throw LibraryInterchangeException("Blank lines are not allowed in the interchange payload")
        }

        val footerFields = normalizedLines.last().split('\t')
        if (footerFields.size != 4 || footerFields[0] != END) {
            throw LibraryInterchangeException("Missing or malformed interchange footer")
        }

        val bodyLines = normalizedLines.dropLast(1)
        val body = bodyLines.joinToString(separator = "\n", postfix = "\n")
        val expectedDigest = footerFields[3]
        val actualDigest = sha256(body)
        if (!MessageDigest.isEqual(expectedDigest.toByteArray(UTF_8), actualDigest.toByteArray(UTF_8))) {
            throw LibraryInterchangeException("Interchange integrity verification failed")
        }

        val expectedWatchCount = footerFields[1].toNonNegativeInt("watch-history count")
        val expectedResumeCount = footerFields[2].toNonNegativeInt("resume-position count")

        val headerFields = bodyLines.first().split('\t')
        if (headerFields.size != 3 || headerFields[0] != FORMAT) {
            throw LibraryInterchangeException("Missing or malformed interchange header")
        }
        val version = headerFields[1].toIntOrNull()
            ?: throw LibraryInterchangeException("Interchange version is not numeric")
        if (version != VERSION) {
            throw LibraryInterchangeException("Unsupported interchange version: $version")
        }
        val exportedAtMs = headerFields[2].toNonNegativeLong("export timestamp")

        val watchHistory = mutableListOf<WatchHistoryEntry>()
        val resumePositions = mutableListOf<ResumePositionEntry>()

        bodyLines.drop(1).forEachIndexed { index, line ->
            val lineNumber = index + 2
            val fields = line.split('\t')
            when (fields.firstOrNull()) {
                WATCH -> watchHistory += parseWatch(fields, lineNumber)
                RESUME -> resumePositions += parseResume(fields, lineNumber)
                else -> throw LibraryInterchangeException(
                    "Unsupported record type on line $lineNumber",
                )
            }
        }

        if (watchHistory.size != expectedWatchCount || resumePositions.size != expectedResumeCount) {
            throw LibraryInterchangeException("Interchange footer counts do not match the payload")
        }

        return try {
            LibrarySnapshotV1(
                exportedAtMs = exportedAtMs,
                watchHistory = watchHistory,
                resumePositions = resumePositions,
            )
        } catch (error: IllegalArgumentException) {
            throw LibraryInterchangeException(error.message ?: "Invalid interchange payload")
        }
    }

    private fun parseWatch(fields: List<String>, lineNumber: Int): WatchHistoryEntry {
        if (fields.size != 7) {
            throw LibraryInterchangeException("Malformed watch-history record on line $lineNumber")
        }
        val completed = when (fields[6]) {
            "0" -> false
            "1" -> true
            else -> throw LibraryInterchangeException(
                "Invalid completion flag on line $lineNumber",
            )
        }
        return try {
            WatchHistoryEntry(
                providerId = decodeToken(fields[1], lineNumber),
                providerVideoId = decodeToken(fields[2], lineNumber),
                firstWatchedAtMs = fields[3].toNonNegativeLong("first watched timestamp", lineNumber),
                lastWatchedAtMs = fields[4].toNonNegativeLong("last watched timestamp", lineNumber),
                playCount = fields[5].toPositiveInt("play count", lineNumber),
                completed = completed,
            )
        } catch (error: IllegalArgumentException) {
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
                positionMs = fields[3].toNonNegativeLong("resume position", lineNumber),
                updatedAtMs = fields[4].toNonNegativeLong("resume updated timestamp", lineNumber),
            )
        } catch (error: IllegalArgumentException) {
            throw LibraryInterchangeException(error.message ?: "Invalid resume-position record")
        }
    }

    private fun encodeToken(value: String): String =
        Base64.getUrlEncoder().withoutPadding().encodeToString(value.toByteArray(UTF_8))

    private fun decodeToken(value: String, lineNumber: Int): String {
        return try {
            val decoded = String(Base64.getUrlDecoder().decode(value), UTF_8)
            if (decoded.isBlank()) {
                throw LibraryInterchangeException("Blank identity token on line $lineNumber")
            }
            decoded
        } catch (error: IllegalArgumentException) {
            if (error is LibraryInterchangeException) throw error
            throw LibraryInterchangeException("Invalid identity token on line $lineNumber")
        }
    }

    private fun sha256(value: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray(UTF_8))
            .joinToString("") { byte -> "%02x".format(byte.toInt() and 0xff) }

    private fun String.toNonNegativeLong(name: String, lineNumber: Int? = null): Long {
        val value = toLongOrNull()
            ?: throw LibraryInterchangeException(formatNumberError(name, lineNumber))
        if (value < 0) {
            throw LibraryInterchangeException("$name must be non-negative${lineSuffix(lineNumber)}")
        }
        return value
    }

    private fun String.toNonNegativeInt(name: String): Int {
        val value = toIntOrNull()
            ?: throw LibraryInterchangeException("$name is not numeric")
        if (value < 0) {
            throw LibraryInterchangeException("$name must be non-negative")
        }
        return value
    }

    private fun String.toPositiveInt(name: String, lineNumber: Int): Int {
        val value = toIntOrNull()
            ?: throw LibraryInterchangeException(formatNumberError(name, lineNumber))
        if (value <= 0) {
            throw LibraryInterchangeException("$name must be positive${lineSuffix(lineNumber)}")
        }
        return value
    }

    private fun formatNumberError(name: String, lineNumber: Int?): String =
        "$name is not numeric${lineSuffix(lineNumber)}"

    private fun lineSuffix(lineNumber: Int?): String =
        lineNumber?.let { " on line $it" }.orEmpty()
}
