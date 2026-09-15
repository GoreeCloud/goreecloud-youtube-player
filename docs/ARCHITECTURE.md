# Architecture

## Current milestone

The native Android foundation, durable local-progress source slice, and Android 16 emulator runtime acceptance are integrated on `main`. The current Development direction is to expand the local-data model through explicit migrations and portable versioning while preserving provider independence and the existing privacy boundary.

This state does **not** establish YouTube provider compatibility, provider authentication, production playback, downloads, casting, Linux support, television acceptance, Platform-System acceptance, or Stable eligibility.

## Boundaries

```text
Android application shell
        |
        v
GoreeCloud UI / navigation boundary
        |
        v
Domain models + capability resolver
        |
        v
ContentProvider contract
        |
        +--> LocalDemoProvider (implemented, network-free, Development only)
        +--> YouTube provider (not implemented)
        +--> future authorized providers

Local state contract
        |
        +--> versioned SQLite schema + explicit migrations
        +--> SQLiteLocalLibraryStore
        |       +--> watch history
        |       +--> resume positions
        |       +--> additional local-library state only after migration/portability acceptance
        |
        +--> LibraryPortabilityService
                +--> versioned GCYTP-LIBRARY validated export/import

Integral Platform Systems
        |
        +--> explicit integration boundaries (blocked/pending)
```

## Provider independence

`ContentProvider` remains the primary provider boundary. Provider-specific networking, identifiers, authorization, media resolution, and failure behavior must remain behind provider-owned implementation code.

`CapabilityResolver` preserves provider declarations and fails missing declarations to `UNKNOWN`. Device, privacy, security, identity, and policy layers may further restrict an operation, but may not upgrade unavailable provider capability.

## Local development provider

`LocalDemoProvider` is deterministic, network-free Development infrastructure. It must never be described as a YouTube implementation.

## Local state

The integrated schema-v1 contract and `SQLiteLocalLibraryStore` persist watch history and resume positions. The Android process opens the database during startup, enables SQLite foreign keys, fails closed on unsupported schema upgrade/downgrade, and exposes schema version plus progress-record counts to the Development UI.

Authoritative `main` commit `71e078c6ee8ed1c1602d388e77c112e669d39431` passed post-merge run `34909976600`, including three Android 16 SQLite instrumentation tests. That evidence establishes the tested emulator-runtime persistence boundary only; physical-device acceptance remains separate.

`LibraryPortabilityService` and `LibraryInterchangeV1Codec` provide deterministic versioned export/import for those progress records. Imported payloads are fully parsed, count-checked, range-checked, duplicate-checked, and SHA-256 integrity-checked before atomic replacement is allowed.

The next local-data architecture increment must introduce explicit approved migration behavior before increasing the database version, preserve backward-compatible import of already exported v1 data, and add new durable state only together with matching persistence, portability, and tests.

Channel follows, playlists, tags, encryption decisions, Everkeep coverage, user-facing conflict choices, and Privacy Shield runtime authorization remain separate work. See [`LOCAL-DATA.md`](LOCAL-DATA.md).

Android automatic backup remains disabled because Everkeep recovery authority is not implemented or accepted.

## User interface and Glaze UI

Jetpack Compose/Material 3 remains an implementation substrate, not proof of Glaze UI conformance. Glaze UI 1.4.0 is the current Official/Stable consumer target. Formal visual, behavioral, accessibility, form-factor, rollback, and runtime acceptance remain pending.

## Privacy and network posture

The current source requests no Android network permission and introduces no provider authentication, YouTube requests, telemetry, remote history sync, remote account state, clipboard monitoring, remote backup, or remote playback resolver.

Any later network capability must establish the required provider, purpose, Privacy Shield, Wardveil, authorization, diagnostics, and testing boundaries first.

## Platform rollout

The product rollout remains:

1. Android phone/tablet.
2. Linux Desktop.
3. Android TV / Google TV.

Linux must be separately validated before support claims. Android TV / Google TV follows Linux and requires purpose-built focus, input, navigation, layout and remote-control acceptance.

## Next architecture increments

1. Add explicit local-database migration/versioning behavior and expand approved local-library persistence.
2. Add user-facing export/import preview, merge/replace choices, and recovery UX.
3. Build search/library UI against provider-neutral interfaces.
4. Establish RSS subscription parsing/refresh behind a feed-provider boundary.
5. Define media-source/playback contracts before remote provider work.
6. Integrate and accept current Glaze UI.
7. Implement applicable Privacy Shield, Wardveil Security, Everkeep, Manager, Mesh, and Identity contracts.
8. Begin Linux expansion after the Android application reaches the required Development maturity.
9. Add Android TV / Google TV after Linux expansion.
