# Local Data Core

## Current implementation state

The integrated Android source binds the existing schema-v1 contract to a runtime SQLite adapter for the first user-owned progress state:

- watch history;
- resume positions.

The remaining schema-v1 tables for channel follows, playlists, tags, and related organizational state remain schema-defined but are not yet bound to the runtime repository API in this slice.

This distinction is intentional. The implementation must not imply that the entire planned local library is complete merely because its schema exists.

## Database authority

`SQLiteLocalLibraryStore` is the current Android runtime persistence boundary.

It:

- opens `goreecloud-youtube-player.db` at schema version 1;
- installs `app/src/main/assets/database/schema-v1.sql` on first creation;
- enables SQLite foreign-key enforcement;
- exposes provider-neutral watch-history and resume-position operations;
- supports atomic replacement of exported/imported progress state;
- fails closed if an unimplemented database upgrade or downgrade is requested.

The database remains local application state. Android automatic backup remains disabled because Everkeep recovery authority is not implemented or accepted for this product.

## Portable progress interchange v1

The first portability format is a deterministic UTF-8 text format named `GCYTP-LIBRARY` version `1`.

It is deliberately dependency-light and provider-neutral. Provider and video identifiers are encoded using URL-safe Base64 without padding so tabs and newlines cannot alter record boundaries.

Structure:

```text
GCYTP-LIBRARY<TAB>1<TAB><exportedAtMs>
W<TAB><provider><TAB><video><TAB><firstWatchedAtMs><TAB><lastWatchedAtMs><TAB><playCount><TAB><completed>
R<TAB><provider><TAB><video><TAB><positionMs><TAB><updatedAtMs>
END<TAB><watchCount><TAB><resumeCount><TAB><sha256-of-body>
```

The codec:

- sorts records deterministically before export;
- validates numeric ranges;
- rejects blank provider/video identities;
- rejects duplicate provider/video identities within a record class;
- rejects unknown record types and unsupported versions;
- verifies declared record counts;
- verifies a SHA-256 integrity digest before accepting imported state.

Integrity verification detects accidental or unreviewed modification. It is not an authenticity signature and must not be represented as one.

## Import semantics

`LibraryPortabilityService.importProgress` fully validates the interchange payload before mutating local state. A validated import atomically replaces the current watch-history and resume-position tables.

Merge/append import modes are not implemented in this slice. User-facing import conflict controls, Everkeep recovery integration, Privacy Shield policy integration, and import previews remain future work.

## Android runtime acceptance candidate

The current Development validation branch adds instrumentation tests that exercise the real SQLite adapter on an Android 16 emulator. The tests verify:

- first-open schema-v1 initialization and empty summary state;
- watch-history and resume-position persistence across database close/reopen;
- replacement progress state surviving reopen while prior state is removed;
- a tampered interchange payload being rejected before replacement and existing persisted progress remaining unchanged.

The associated CI job records and verifies the exact source revision before starting the emulator, runs `:app:connectedDebugAndroidTest`, and preserves emulator/test evidence as a workflow artifact.

This is not accepted runtime evidence until the exact candidate passes and the validated source is intentionally integrated and post-merge verified.

## Privacy and network posture

This local-data slice adds no application network permission, remote account dependency, telemetry, cloud synchronization, remote backup, provider authentication, or external data transmission.

Future remote synchronization or backup work must establish its own Identity, Privacy Shield, Wardveil Security, Everkeep, and provider authorization boundaries before being exposed as available.

## Validation boundary

Passing JVM tests, Android lint, APK assembly, or instrumentation tests proves only the behavior each check actually exercises. Runtime emulator evidence does not establish physical-device acceptance, complete local-library behavior, approved schema migrations beyond v1, recovery acceptance, release readiness, production acceptance, or Stable qualification.
