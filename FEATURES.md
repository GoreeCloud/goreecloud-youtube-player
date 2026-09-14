# GoreeCloud YouTube Player — Features

This file separates **verified repository features** from **planned product features**. Planned scope must not be presented as implemented behavior.

## Implemented in the current foundation candidate

- Native Android application shell using Kotlin and Jetpack Compose.
- GoreeCloud-owned package identity: `com.goreecloud.youtubeplayer` with Development `.dev` suffix.
- Provider-neutral `Video` domain model.
- Explicit provider capability states including available/supported/unsupported/offline/restricted/permission-required/temporarily-unavailable/degraded/disabled/unknown.
- Capability resolver that fails missing provider declarations to `UNKNOWN`.
- `ContentProvider` abstraction.
- Deterministic, network-free `LocalDemoProvider` for development testing.
- Versioned schema-v1 contract for local library-oriented state.
- Machine-readable provider-capability and GoreeCloud platform-system contracts.
- Development UI that exposes provider capability decisions rather than pretending unavailable functionality exists.
- Android validation workflow definition for unit tests, lint, development APK assembly, package identity and a no-`INTERNET` permission guard.
- Android automatic backup disabled while Everkeep/recovery authority remains unimplemented.

## In active stacked development, not yet integrated into the foundation

Milestone 2 / PR #2 is developing:

- runtime SQLite binding for schema-v1;
- durable watch-history persistence;
- durable resume-position persistence;
- versioned portable progress export/import;
- deterministic interchange encoding and integrity validation;
- atomic replacement of validated imported progress state.

These items remain dependent on their own review and exact-head validation.

## Planned product features

The canonical product specification plans:

- customizable Home experience;
- native video, channel, playlist and local-library search;
- native channel pages;
- RSS-based subscriptions without requiring a Google account where supported;
- chronological Subscription Inbox;
- native video player with seeking, chapters, speed, captions, audio/quality selection where supported;
- picture-in-picture and policy/capability-aware background playback;
- persistent local playback queue;
- local library, custom playlists and collections;
- Watch Later, favorites, watch history and Continue Watching;
- explicit Shorts controls;
- live-stream and premiere awareness;
- optional live chat through approved provider interfaces;
- granular upload/live/premiere/RSS notifications;
- optional sponsor/segment metadata integration through bounded interfaces;
- local timestamp-aware notes and tags;
- capability-aware offline media and downloads where permitted;
- local media playback;
- supported URL handling, share targets and privacy-bounded clipboard recognition;
- capability-aware casting;
- Android phone/tablet integration;
- purpose-built Android TV / Google TV interface;
- planned Linux desktop client;
- keyboard, touch, remote-control and accessibility adaptation;
- provider failure isolation, caching, diagnostics and explainable capability decisions;
- optional GoreeCloud Manager, Privacy Shield, Wardveil, Everkeep, Mesh and Identity integrations after verified implementation.

## Current non-features

The current foundation must not be represented as providing YouTube authentication, remote YouTube search, production playback, downloads, casting, cloud synchronization, telemetry, privacy/security enforcement, Glaze conformance, Linux support, television acceptance, release readiness or Stable status.
