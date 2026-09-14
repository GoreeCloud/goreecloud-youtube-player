# GoreeCloud YouTube Player

GoreeCloud YouTube Player is an original GoreeCloud-owned native application for discovering, organizing, and playing supported video content through explicit provider interfaces.

## Development status

**Lifecycle:** Active Development / pre-Stable  
**Repository:** `GoreeCloud/goreecloud-youtube-player`  
**Initial availability target:** Android phones/tablets  
**First planned expansion:** Linux Desktop  
**Second planned expansion:** Android TV / Google TV  
**Additional platforms:** Require a separate product decision and authorization  
**Glaze UI target:** 1.3.0 — current published Official/Stable/consumer-eligible release; application integration and conformance are not yet validated

The Android foundation and first durable local-data source slice are integrated on `main`. The current Development work adds Android-emulator acceptance for that persistence boundary without expanding provider, network, account, or production authority.

## Integrated Android source

- Native Android application shell using Kotlin and Jetpack Compose.
- Provider-independent video domain model and explicit capability decisions.
- `ContentProvider` boundary and deterministic, network-free `LocalDemoProvider`.
- Versioned schema-v1 contract and runtime `SQLiteLocalLibraryStore`.
- Durable watch-history and resume-position persistence operations.
- Atomic replacement of validated imported progress state.
- Deterministic `GCYTP-LIBRARY` v1 UTF-8 export/import with SHA-256 integrity, count, range, identity, and duplicate validation.
- Development UI visibility of local schema version and progress counts.
- Exact-source Android CI with unit tests, lint, APK identity checks, and the no-`INTERNET` guard.
- Android automatic backup disabled while Everkeep recovery authority remains unimplemented.

## Current runtime-acceptance candidate

The current Development branch adds an Android 16 emulator gate that exercises the real SQLite adapter and verifies:

- schema-v1 initialization;
- persistence across database close/reopen;
- successful replacement of progress state across reopen;
- rejected/tampered imports leaving persisted progress unchanged.

This runtime evidence is not accepted until the exact pull-request head passes the emulator job and the resulting source is intentionally integrated and post-merge validated.

## Architecture principles

1. GoreeCloud owns the application experience and product-defining implementation.
2. Provider-specific behavior remains behind explicit provider contracts.
3. Missing provider capability fails to `UNKNOWN` rather than being silently upgraded.
4. User-organized state is local-first wherever practical.
5. UI claims must reflect accepted runtime capability and Platform-System evidence.
6. Glaze UI 1.3.0 is the current consumer target, but application conformance remains unaccepted.
7. Privacy Shield, Wardveil Security, Everkeep, Manager, Mesh and Identity remain blocked until implemented and validated.
8. Product rollout remains Android → Linux Desktop → Android TV / Google TV.

See [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md), [`docs/LOCAL-DATA.md`](docs/LOCAL-DATA.md), and [`docs/PLATFORM-INTEGRATIONS.md`](docs/PLATFORM-INTEGRATIONS.md).

## Build validation

CI validates the exact candidate revision with Java 17 and Gradle 9.5.0. The standard job runs unit tests, lint, development APK assembly, package/version/label verification, and the no-`INTERNET` guard. The current runtime-acceptance candidate additionally runs Android instrumentation tests on an Android 16 emulator after the standard job succeeds.

A green Development workflow proves only the checks it actually ran. It does not establish release, Stable, Glaze UI, Privacy Shield, Wardveil, Everkeep, provider, Linux, television, or production acceptance.

## Licensing

No public software license has yet been approved for this product. See [`LICENSE`](LICENSE).
