# GoreeCloud YouTube Player — Repository Specification

## Authority and lifecycle

**Product:** GoreeCloud YouTube Player  
**Repository:** `GoreeCloud/goreecloud-youtube-player`  
**Repository lifecycle:** Active Development / pre-Stable  
**Canonical product specification:** GoreeCloud Drive → Projects → `Project Specification — YouTube Player.docx`  
**Current executable target:** Android phone/tablet Development foundation  
**Mandatory delivery targets:** Android application, first-class web application, supported Linux deployment  
**Additional planned form factor:** Android TV / Google TV  
**Current published Glaze UI target:** 1.3.0; application integration and conformance are not yet accepted

This file is the repository-local, version-coupled summary of the canonical GoreeCloud product specification and current governing delivery requirements. When this file conflicts with higher-authority GoreeCloud governance or the canonical Drive specification, the controlling authoritative requirement governs and this file must be reconciled.

## Product purpose

GoreeCloud YouTube Player is intended to be a native, privacy-conscious GoreeCloud application for discovering, organizing, and watching supported YouTube content without allowing a provider website to define the product experience.

The governing product principle is:

> GoreeCloud owns the application experience. Providers supply bounded content and capabilities. Glaze UI presents accepted capabilities truthfully.

The product must remain GoreeCloud-owned software rather than a WebView wrapper or inherited complete third-party application codebase.

## Delivery model

The current implementation begins with native Android. GoreeCloud delivery governance also requires a first-class web application and a supported Linux deployment. These are Development obligations, not claims that those clients already exist.

Android TV / Google TV is an additional planned form factor and must receive focus/input/layout behavior appropriate to television rather than a stretched phone interface.

Native clients may share domain models, APIs, protocols, design tokens, data formats, and non-UI components, but platform-specific clients must not be reduced to web wrappers for code-reuse convenience.

## Required architecture boundaries

### Native application boundary

Navigation, application state, library, settings, search surfaces, history, playlists, subscriptions, notifications, and player controls are intended to be GoreeCloud-owned experiences.

### Provider boundary

Provider-specific networking, identifiers, authentication, metadata retrieval, media resolution, captions, and failure behavior must remain behind explicit provider interfaces.

Provider capabilities must be explicit. Missing declarations fail to `UNKNOWN`; UI presence must never upgrade an unsupported or unknown provider capability.

### Local-first state boundary

User-organized state should remain locally controlled wherever practical. Planned local state includes watch history, resume positions, favorites, bookmarks, custom playlists, channel follows, search history, notes, tags, feed organization, and UI preferences.

The foundation source defines schema-v1 for local library-oriented state. Runtime persistence and portable interchange are being developed separately in stacked Milestone 2 work and are not part of the foundation candidate until independently validated and integrated.

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

All seven remain unaccepted for this application. Glaze UI 1.3.0 is the current required consumer target, but targeting a version is not conformance. A contract file, UI label, dependency, or planned integration is not evidence of runtime acceptance.

## Foundation implementation state

The current foundation candidate implements:

- native Android application shell using Kotlin and Jetpack Compose;
- provider-neutral video domain model;
- explicit provider capability states and decisions;
- `ContentProvider` contract;
- deterministic, network-free `LocalDemoProvider` for Development only;
- versioned local SQLite schema contract;
- language-neutral provider and platform-system contracts;
- exact-source Android validation workflow;
- truthful platform-integration status documentation.

The foundation does **not** implement or prove:

- YouTube authentication or network retrieval;
- production media playback;
- RSS subscription refresh;
- downloads/offline YouTube media;
- casting;
- web application implementation;
- Android TV acceptance;
- Linux client implementation;
- telemetry;
- Privacy Shield enforcement;
- Wardveil enforcement;
- Everkeep recovery;
- GoreeCloud Identity, Mesh, or Manager runtime integration;
- Glaze UI conformance;
- release, production, or Stable qualification.

## Planned capability families

The canonical product specification plans native Home/discovery, search, channels, RSS subscriptions, chronological subscription inbox, media playback, picture-in-picture, queues, local library/organization, Shorts controls, live awareness, notifications, notes/tags, authorized offline/local-media behavior, URL/share integration, casting, Android TV, Linux, accessibility/input adaptation, provider failure isolation, caching, diagnostics, and evidence-backed GoreeCloud ecosystem integration.

The mandatory delivery roadmap additionally includes a first-class web client. None of these planned capability families may be represented as implemented merely because they appear in documentation.

## Stable eligibility

A successful source build is Development evidence only. Stable eligibility requires the exact release candidate to satisfy all applicable native-development, current Glaze UI, accessibility, privacy, security, resilience, platform, signing/distribution, documentation, observability, recovery, physical-device, and production-readiness gates.
