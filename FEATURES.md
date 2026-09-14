# GoreeCloud YouTube Player — Features

This file separates **integrated capability**, **current Development candidate behavior**, and **planned product scope**.

## Integrated Android foundation

- Native Android application shell using Kotlin and Jetpack Compose.
- Development package identity using `com.goreecloud.youtubeplayer.dev`.
- Provider-neutral `Video` domain model.
- Explicit provider capability states and fail-to-`UNKNOWN` resolution.
- `ContentProvider` abstraction.
- Deterministic, network-free `LocalDemoProvider`.
- Versioned schema-v1 contract for local library-oriented state.
- Machine-readable provider and Platform-System contracts.
- Development UI for capability decisions.
- Exact-source Android validation workflow.
- Android automatic backup disabled pending accepted recovery authority.

## Current Development candidate — PR #2

- Runtime SQLite schema-v1 initialization.
- Durable watch-history persistence.
- Durable resume-position persistence.
- Atomic replacement of validated imported progress state.
- Deterministic `GCYTP-LIBRARY` v1 UTF-8 export/import.
- SHA-256 integrity verification plus count, range, identity, and duplicate validation.
- Development UI visibility of schema version and local progress counts.
- Unit coverage for interchange and portability-service behavior.

These items are not integrated until the exact PR #2 candidate passes review and exact-head validation and is intentionally merged.

## Planned product features

Planned scope includes Home/discovery, provider-neutral search, native channel pages, RSS subscriptions and chronological inbox, production playback, PiP/background playback where allowed, queues, playlists/collections, Watch Later/favorites/history/Continue Watching, Shorts controls, live/premiere awareness, notifications, notes/tags, authorized offline/local media, URL/share handling, casting, accessibility/input adaptation, Linux expansion, Android TV / Google TV expansion, provider failure isolation, caching/diagnostics, and evidence-backed GoreeCloud Platform-System integration.

## Current non-features

The application must not currently be represented as providing YouTube authentication, remote YouTube retrieval, production playback, downloads, casting, cloud synchronization, telemetry, accepted Privacy Shield/Wardveil/Everkeep integration, Glaze UI conformance, Linux support, television support, release readiness, production acceptance, or Stable status.
