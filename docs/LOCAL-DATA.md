# Local Data Core

## Current implementation state

The Milestone 2 development branch binds the existing schema-v1 contract to a runtime SQLite adapter for the first user-owned progress state:

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

## Privacy and network posture

This local-data slice adds no network permission, remote account dependency, telemetry, cloud synchronization, remote backup, provider authentication, or external data transmission.

Future remote synchronization or backup work must establish its own Identity, Privacy Shield, Wardveil Security, Everkeep, and provider authorization boundaries before being exposed as available.

## Validation boundary

Passing source tests or successfully opening the schema proves only the tested local-data behavior. It does not establish full library completion, migration acceptance, recovery acceptance, release readiness, production acceptance, or Stable qualification.
