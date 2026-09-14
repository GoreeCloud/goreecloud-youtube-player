# GoreeCloud YouTube Player — Repository Specification

## Authority and lifecycle

**Product:** GoreeCloud YouTube Player  
**Repository:** `GoreeCloud/goreecloud-youtube-player`  
**Repository lifecycle:** Active Development / pre-Stable  
**Canonical product specification:** GoreeCloud Drive → Projects → `Project Specification — YouTube Player.docx`  
**Current executable target:** Android phone/tablet development foundation  
**Planned primary platforms:** Android, Android TV / Google TV, Linux Desktop  
**Current published Glaze UI target:** 1.3.0; application integration and conformance are not yet accepted

This file is the repository-local, version-coupled summary of the canonical GoreeCloud product specification. The Drive specification remains the controlling product/architecture record when this summary and the canonical document differ.

## Product purpose

GoreeCloud YouTube Player is intended to be a native, privacy-conscious GoreeCloud application for discovering, organizing, and watching supported YouTube content without allowing a provider website to define the product experience.

The governing product principle is:

> GoreeCloud owns the application experience. Providers supply bounded content and capabilities. Glaze UI presents accepted capabilities truthfully.

The application is designed to remain GoreeCloud-owned software rather than a WebView wrapper or inherited complete third-party application codebase.

## Required architecture boundaries

### Native application boundary

Navigation, application state, library, settings, search surfaces, history, playlists, subscriptions, notifications, and player controls are intended to be native GoreeCloud experiences.

### Provider boundary

Provider-specific networking, identifiers, authentication, metadata retrieval, media resolution, captions, and failure behavior must remain behind explicit provider interfaces.

Provider capabilities must be explicit. Missing declarations fail to `UNKNOWN`; UI presence must never upgrade an unsupported or unknown provider capability.

### Local-first state boundary

User-organized state should remain locally controlled wherever practical. Planned local state includes watch history, resume positions, favorites, bookmarks, custom playlists, channel follows, search history, notes, tags, feed organization, and UI preferences.

The foundation source currently defines schema-v1 for local library-oriented state. Runtime persistence and portable interchange are being developed separately in stacked Milestone 2 work and are not part of the foundation merge candidate until independently validated and integrated.

### Network and privacy boundary

The foundation requests no Android `INTERNET` permission and contains no provider authentication, YouTube networking, remote telemetry, remote history synchronization, clipboard monitoring, or remote playback resolver.

Network access may be introduced only when the relevant provider, purpose, authorization, Privacy Shield, Wardveil Security, diagnostics, and testing boundaries are explicitly established.

### Integral Platform Systems

The architecture evaluates all seven GoreeCloud Integral Platform Systems independently:

1. GoreeCloud Manager
2. Privacy Shield
3. Wardveil Security
4. Everkeep
5. Glaze UI
6. GoreeCloud Mesh
7. GoreeCloud Identity

Except for the documented Glaze target, these remain planned integration boundaries in the foundation. A contract file, UI label, or dependency name is not evidence of runtime acceptance.

## Foundation implementation state

The current foundation candidate implements:

- native Android application shell using Kotlin and Jetpack Compose;
- provider-neutral video domain model;
- explicit provider capability states and decisions;
- `ContentProvider` contract;
- deterministic, network-free `LocalDemoProvider` for development only;
- versioned local SQLite schema contract;
- language-neutral provider and platform-system contracts;
- Android validation workflow definition;
- truthful platform-integration status documentation.

The foundation does **not** implement or prove:

- YouTube authentication or network retrieval;
- production media playback;
- RSS subscription refresh;
- downloads/offline YouTube media;
- casting;
- Android TV acceptance;
- Linux client implementation;
- telemetry;
- Privacy Shield enforcement;
- Wardveil enforcement;
- Everkeep recovery;
- GoreeCloud Identity or Mesh integration;
- Glaze UI conformance;
- release, production, or Stable qualification.

## Planned product capability families

The canonical product specification plans capability families including:

- native Home and discovery surfaces;
- native video/channel/playlist/local-library search;
- native channel profiles;
- RSS-based channel following and chronological subscription inbox;
- native playback controls, seeking, speed control, captions, quality selection and media integration where supported;
- picture-in-picture and background playback where permitted;
- queue management;
- local library, playlists, collections, Watch Later, favorites, history and Continue Watching;
- Shorts controls, live-stream awareness and optional live chat;
- granular notifications;
- local notes and tags;
- authorized offline/local-media behavior;
- URL/share integration and capability-aware casting;
- Android phone/tablet and purpose-built Android TV / Google TV experiences;
- Linux desktop support;
- accessibility, keyboard, touch and remote-control adaptation;
- provider capability resolution, failure isolation, cache/offline behavior and diagnostics;
- optional GoreeCloud ecosystem integrations only after their own implementation and acceptance gates.

These are planned requirements, not claims that the current source implements them.

## Stable eligibility

A successful source build is development evidence only. Stable eligibility requires the exact release candidate to satisfy all applicable GoreeCloud release, current Glaze UI, accessibility, privacy, security, resilience, platform, signing/distribution, documentation, observability, physical-device, and production-readiness gates.
