# GoreeCloud YouTube Player

GoreeCloud YouTube Player is an original GoreeCloud-owned native application for discovering, organizing, and playing supported video content through explicit provider interfaces.

## Development status

**Lifecycle:** Active Development / pre-Stable  
**Repository:** `GoreeCloud/goreecloud-youtube-player`  
**Repository recovery:** Repository restored September 14, 2026; default-branch CI recovery has been integrated and exact-head validated  
**Current executable target:** Android phones/tablets Development foundation  
**Mandatory planned delivery targets:** Android, first-class web application, supported Linux deployment  
**Additional planned form factor:** Android TV / Google TV  
**Glaze UI target:** 1.3.0 — current published Official/Stable/consumer-eligible release; application integration and conformance are not yet validated

This repository is not a fork of another video application and does not wrap the YouTube website as its application architecture.

The current foundation intentionally contains no YouTube authentication, scraping, remote playback, remote telemetry, or provider-specific network implementation. It establishes the native Android shell, domain/provider contracts, capability resolution, a deterministic local development provider, a local database schema baseline, truthful Platform-System state, and exact-source CI so later provider work can be added behind controlled boundaries.

## Current implementation slice

- Native Android application shell using Kotlin and Jetpack Compose.
- Provider-independent video domain model.
- Explicit provider capability states and explainable capability decisions.
- `ContentProvider` boundary and deterministic local development provider.
- Versioned local SQLite schema contract for library-oriented state.
- Language-neutral provider capability contract under `contracts/`.
- GoreeCloud Integral Platform System integration-status record.
- Android unit-test, lint, and debug-build workflow.
- No Android `INTERNET` permission in the foundation application.
- Android automatic backup disabled while Everkeep recovery authority remains unimplemented.

## Architecture principles

1. GoreeCloud owns the application experience and product-defining implementation.
2. Providers expose bounded content/capability contracts; provider implementation details do not define the app.
3. Missing provider capability is treated as `UNKNOWN`, never silently upgraded to supported.
4. Local organizational state is designed to remain useful independently of remote provider availability.
5. UI claims must reflect accepted runtime capability and Platform-System evidence.
6. Glaze UI 1.3.0 is the current published Official/Stable/consumer-eligible design-system target for this revision, but the foundation is not Glaze-conformant until integration and acceptance are verified.
7. Privacy Shield, Wardveil Security, Everkeep, GoreeCloud Manager, GoreeCloud Mesh, and GoreeCloud Identity remain blocked/planned integration boundaries until implemented and validated.
8. Mandatory future web and Linux delivery paths must share authoritative domain/data contracts without turning native clients into web wrappers.

## Repository documentation

- [`SPECIFICATIONS.md`](SPECIFICATIONS.md) — repository-local, version-coupled product and architecture requirements.
- [`FEATURES.md`](FEATURES.md) — implemented versus planned capabilities.
- [`FEATURE-ROADMAP.md`](FEATURE-ROADMAP.md) — ordered Development roadmap synchronized with the central GoreeCloud roadmap record.
- [`BENEFITS.md`](BENEFITS.md) — current architectural benefits and intended user benefits.
- [`COMPETITIVE-OBJECTIVES.md`](COMPETITIVE-OBJECTIVES.md) — product objectives without unsupported superiority claims.
- [`BRANDING.md`](BRANDING.md) — canonical identity boundary; product-specific artwork is not yet approved.
- [`USER-MANUAL.md`](USER-MANUAL.md) — current Development behavior only.
- [`PRIVACY POLICY.md`](PRIVACY%20POLICY.md) — current source-level privacy behavior and future authority boundary.
- [`SECURITY.md`](SECURITY.md) — security posture, reporting boundary, and non-claims.
- [`NOTES.md`](NOTES.md) — development/recovery notes and known limitations.
- [`goreecloud.platform.yaml`](goreecloud.platform.yaml) — machine-readable Platform-System truth.
- [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) — current source architecture.
- [`docs/PLATFORM-INTEGRATIONS.md`](docs/PLATFORM-INTEGRATIONS.md) — truthful Integral Platform System status.

## Build validation

CI validates the exact candidate revision with Java 17 and Gradle 9.5.0, then runs:

```text
gradle :app:testDebugUnitTest
gradle :app:lintDebug
gradle :app:assembleDebug
```

The workflow also verifies the Development APK identity and rejects accidental `android.permission.INTERNET` in this foundation stage.

A successful source build is Development evidence only. It is not Stable qualification, production acceptance, Glaze UI acceptance, Privacy Shield acceptance, Wardveil acceptance, Everkeep recovery acceptance, or supported-provider acceptance.

## Licensing

No public software license has yet been approved for this product. See [`LICENSE`](LICENSE). Public repository visibility must not be interpreted as an unrecorded license grant.
