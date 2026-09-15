# Local Data Core

## Accepted baseline

Authoritative `main` commit `71e078c6ee8ed1c1602d388e77c112e669d39431` has accepted Android 16 emulator evidence for the schema-v1 progress boundary. Post-merge workflow run `34909976600` passed the standard Android validation job and three SQLite instrumentation tests covering first-open initialization, persistence across reopen, replacement semantics, and rejected-import state preservation.

That evidence remains valid for the integrated v1 baseline. Physical-device acceptance is still separate.

## Schema-v2 Development candidate

PR #5 expands the local database through an explicit v1 → v2 migration instead of destructive recreation.

The candidate binds four provider-neutral record classes:

- watch history;
- resume positions;
- favorites;
- Watch Later.

The existing schema-defined channel follows, playlists, tags, and related organizational state remain outside the runtime repository API in this slice. Their presence in the database schema does not imply feature completion.

## Database authority

`SQLiteLocalLibraryStore` remains the Android runtime persistence boundary.

The schema-v2 candidate:

- opens `goreecloud-youtube-player.db` at database version 2;
- installs `app/src/main/assets/database/schema-v2.sql` on a new database;
- retains the historical `schema-v1.sql` contract for migration evidence;
- executes only the reviewed `migration-v1-to-v2.sql` path when upgrading version 1 to version 2;
- fails closed for all other unapproved upgrades and for downgrades;
- enables SQLite foreign-key enforcement;
- exposes provider-neutral watch-history, resume-position, Favorites, and Watch Later operations;
- atomically replaces all four portable record classes after import validation.

Android automatic backup remains disabled because Everkeep recovery authority is not implemented or accepted.

## Portable interchange

`GCYTP-LIBRARY` v1 remains a supported import format for previously exported Development data.

The schema-v2 candidate exports `GCYTP-LIBRARY` version 2. V2 retains the v1 watch-history (`W`) and resume-position (`R`) records and adds Favorites (`F`) and Watch Later (`L`). Its footer includes counts for all four record classes plus the SHA-256 body digest.

```text
GCYTP-LIBRARY<TAB>2<TAB><exportedAtMs>
W<TAB><provider><TAB><video><TAB><firstWatchedAtMs><TAB><lastWatchedAtMs><TAB><playCount><TAB><completed>
R<TAB><provider><TAB><video><TAB><positionMs><TAB><updatedAtMs>
F<TAB><provider><TAB><video><TAB><addedAtMs>
L<TAB><provider><TAB><video><TAB><addedAtMs>
END<TAB><watchCount><TAB><resumeCount><TAB><favoriteCount><TAB><watchLaterCount><TAB><sha256-of-body>
```

The v2 codec sorts each record class deterministically and validates numeric ranges, identities, duplicates, record types, record counts, supported version, and SHA-256 integrity before persistent replacement.

Integrity verification detects accidental or unreviewed modification. It is not an authenticity signature.

## Backward-compatible import behavior

The version-dispatch layer accepts v1 and v2 payloads. A valid v1 payload is upgraded in memory to a v2 snapshot with empty Favorites and Watch Later collections.

Current import behavior remains explicit whole-library **replace** semantics. Therefore importing a v1 payload into a v2 store clears any current Favorites or Watch Later state. This is deterministic low-level behavior, not yet a user-facing product decision.

User-facing preview plus explicit merge/replace choices remain the next portability UX gate and must make this effect understandable before a destructive replacement is confirmed.

## Candidate validation requirements

PR #5 must prove on its exact final revision:

- new schema-v2 database initialization;
- persistence of all four bound record classes across database close/reopen;
- migration of a real schema-v1 fixture to v2 without losing existing watch-history/resume state;
- ability to write/read new Favorites and Watch Later state after migration;
- deterministic v2 export/import;
- successful import of legacy v1 exports;
- atomic v2 replacement semantics;
- rejection of tampered data before mutation;
- the existing no-`INTERNET` privacy guard and standard Android build/lint/test gates.

No v2 behavior is integrated or accepted until the exact candidate passes these checks, is intentionally merged, and the resulting `main` revision is post-merge validated.

## Privacy and network posture

This work adds no application network permission, remote account dependency, telemetry, cloud synchronization, remote backup, provider authentication, or external data transmission.

Future remote synchronization or backup work must establish its own Identity, Privacy Shield, Wardveil Security, Everkeep, and provider authorization boundaries before being exposed as available.

## Remaining local-data gates

- Physical-device acceptance for the durable local-data behavior.
- User-facing export/import preview, merge/replace controls, accessible error handling, and recovery UX.
- Further approved local-library persistence with matching migrations and portability coverage.
- Privacy Shield runtime authorization.
- Everkeep backup, restore, and recovery acceptance.
- Production/release/Stable qualification.
