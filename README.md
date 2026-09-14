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

The Android foundation is integrated on `main`. This Development candidate adds the first durable local-data runtime slice while preserving the provider-independent, network-minimal architecture.

## Integrated foundation

- Native Android application shell using Kotlin and Jetpack Compose.
- Provider-independent video domain model and explicit capability decisions.
- `ContentProvider` boundary and deterministic, network-free `LocalDemoProvider`.
- Versioned schema-v1 contract for local library-oriented state.
- Exact-source Android CI with unit tests, lint, APK identity checks and no-`INTERNET` guard.
- Android automatic backup disabled while Everkeep recovery authority remains unimplemented.

## Current local-data candidate

PR #2 adds:

- runtime schema-v1 initialization through `SQLiteLocalLibraryStore`;
- durable watch-history and resume-position persistence;
- atomic replacement of validated imported progress state;
- deterministic `GCYTP-LIBRARY` v1 UTF-8 export/import;
- SHA-256 integrity, record-count, numeric/range, identity and duplicate validation;
- Development UI visibility of schema version and local progress counts;
- unit coverage for interchange and portability behavior.

These local-data capabilities remain Development candidate behavior until PR #2 passes exact-head validation and is intentionally integrated.

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

CI validates the exact candidate revision with Java 17 and Gradle 9.5.0 and runs unit tests, lint, development APK assembly, package/version/label verification, and the no-`INTERNET` guard.

A green Development build does not establish release, Stable, Glaze UI, Privacy Shield, Wardveil, Everkeep, provider, or production acceptance.

## Licensing

No public software license has yet been approved for this product. See [`LICENSE`](LICENSE).
