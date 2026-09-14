# GoreeCloud YouTube Player

GoreeCloud YouTube Player is an original GoreeCloud-owned native application for discovering, organizing, and playing supported video content through explicit provider interfaces.

## Development status

**Lifecycle:** Active Development / pre-Stable  
**Repository:** `GoreeCloud/goreecloud-youtube-player`  
**Repository recovery:** Restored on September 14, 2026 after deletion; post-restoration exact-head CI must pass before integration  
**Initial executable target:** Android phones/tablets; Android TV/Google TV adaptation follows the Android foundation  
**Planned additional target:** Linux desktop  
**Glaze UI target:** 1.4.0 (integration and conformance are not yet validated)

This repository is not a fork of another video application and does not wrap the YouTube website as its application architecture.

The current bootstrap intentionally contains no YouTube authentication, scraping, remote playback, remote telemetry, or provider-specific network implementation. It establishes the native application shell, domain/provider contracts, capability resolution, a deterministic local development provider, a local database schema baseline, and CI validation so later provider work can be added behind controlled boundaries.

## Current implementation slice

- Native Android application shell using Kotlin and Jetpack Compose.
- Provider-independent video domain model.
- Explicit provider capability states and explainable capability decisions.
- `ContentProvider` boundary and deterministic local development provider.
- Versioned local SQLite schema contract for library-oriented state.
- Language-neutral provider capability contract under `contracts/`.
- GoreeCloud Integral Platform System integration-status record.
- Android unit-test, lint, and debug-build workflow.
- No unnecessary network permission in the bootstrap application.

## Architecture principles

1. GoreeCloud owns the application experience and product-defining implementation.
2. Providers expose bounded content/capability contracts; provider implementation details do not define the app.
3. Missing provider capability is treated as `UNKNOWN`, never silently upgraded to supported.
4. Local organizational state is designed to remain useful independently of remote provider availability.
5. UI claims must reflect accepted runtime capability and platform-system evidence.
6. Glaze UI 1.4.0 is the current design-system target, but this bootstrap must not be described as Glaze-conformant until its integration and acceptance are verified.
7. Privacy Shield, Wardveil Security, Everkeep, GoreeCloud Manager, GoreeCloud Mesh, and GoreeCloud Identity remain planned integration boundaries until implemented and validated.

See [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) and [`docs/PLATFORM-INTEGRATIONS.md`](docs/PLATFORM-INTEGRATIONS.md).

## Build validation

The repository CI is designed to validate the exact pull-request source revision with Java 17 and Gradle 9.5.0, then run:

```text
gradle :app:testDebugUnitTest
gradle :app:lintDebug
gradle :app:assembleDebug
```

A successful source build is development evidence only. It is not Stable qualification, production acceptance, Glaze UI acceptance, Privacy Shield acceptance, Wardveil acceptance, Everkeep recovery acceptance, or supported-provider acceptance.
