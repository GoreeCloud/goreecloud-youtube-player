# GoreeCloud YouTube Player

GoreeCloud YouTube Player is an original GoreeCloud-owned native application for discovering, organizing, and playing supported video content through explicit provider interfaces.

## Development status

**Lifecycle:** Active Development / pre-Stable  
**Repository:** `GoreeCloud/goreecloud-youtube-player`  
**Initial availability target:** Android phones/tablets  
**First planned expansion:** Linux Desktop  
**Second planned expansion:** Android TV / Google TV  
**Additional platforms:** Require a separate product decision and authorization  
**Glaze UI target:** 1.4.0 — current Official/Stable/consumer-eligible shared design-system release; application integration and conformance are not yet validated

The Android foundation, first durable local-data source slice, and Android 16 emulator runtime acceptance are integrated on `main`. The tested runtime boundary covers SQLite schema-v1 initialization, persistence across reopen, replacement semantics, and rejected-import state preservation without expanding provider, network, account, or production authority.

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
- Android 16 emulator instrumentation coverage for the integrated SQLite persistence boundary.
- Android automatic backup disabled while Everkeep recovery authority remains unimplemented.

## Verified runtime boundary

Authoritative `main` commit `71e078c6ee8ed1c1602d388e77c112e669d39431` passed post-merge workflow run `34909976600`, including the standard Android validation job and the Android runtime local-data acceptance job. The emulator job completed three SQLite instrumentation tests on Android 16.

That evidence establishes only the tested emulator behavior. Physical-device acceptance, database migrations beyond schema v1, broader local-library persistence, user-facing import/export, Privacy Shield authorization, Everkeep recovery, provider networking, release, production, and Stable qualification remain separate gates.

## Architecture principles

1. GoreeCloud owns the application experience and product-defining implementation.
2. Provider-specific behavior remains behind explicit provider contracts.
3. Missing provider capability fails to `UNKNOWN` rather than being silently upgraded.
4. User-organized state is local-first wherever practical.
5. UI claims must reflect accepted runtime capability and Platform-System evidence.
6. Glaze UI 1.4.0 is the current consumer target, but application conformance remains unaccepted.
7. Privacy Shield, Wardveil Security, Everkeep, Manager, Mesh and Identity remain blocked until implemented and validated.
8. Product rollout remains Android → Linux Desktop → Android TV / Google TV.

See [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md), [`docs/LOCAL-DATA.md`](docs/LOCAL-DATA.md), and [`docs/PLATFORM-INTEGRATIONS.md`](docs/PLATFORM-INTEGRATIONS.md).

## Build validation

CI validates the exact candidate revision with Java 17 and Gradle 9.5.0. The standard job runs unit tests, lint, development APK assembly, package/version/label verification, and the no-`INTERNET` guard. The runtime job additionally runs Android instrumentation tests on an Android 16 emulator after the standard job succeeds.

A green Development workflow proves only the checks it actually ran. It does not establish release, Stable, Glaze UI, Privacy Shield, Wardveil, Everkeep, provider, Linux, television, or production acceptance.

## Licensing

No public software license has yet been approved for this product. See [`LICENSE`](LICENSE).
