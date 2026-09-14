# GoreeCloud YouTube Player — Development User Manual

**Lifecycle:** Development  
**Scope:** current verified Android foundation only

This manual intentionally documents only behavior that exists in the current foundation candidate. Planned YouTube playback, account, RSS, library, web, television, and Linux features are not described as available.

## Current availability

There is no accepted Stable or production release. The current source produces a Development Android application when exact-source CI/build validation succeeds.

Development package identity:

- application ID: `com.goreecloud.youtubeplayer.dev`
- label: `GoreeCloud YouTube Player Dev`
- version: `0.1.0-dev`

## What the Development app currently does

On launch, the app presents a simple native Android development surface backed by `LocalDemoProvider`.

The screen shows:

- the Development app title;
- the current deterministic local provider name;
- the Glaze UI target and the fact that conformance is pending;
- provider capability decisions and their reasons.

The local development provider is network-free. It exists to exercise GoreeCloud-owned domain/provider architecture and must not be interpreted as a YouTube implementation.

## What is not available

The current foundation does not provide:

- YouTube sign-in or account access;
- remote YouTube search or metadata retrieval;
- production video playback;
- RSS subscriptions;
- casting;
- downloads;
- persistent runtime watch history/resume state in the foundation candidate;
- cloud synchronization;
- Android TV acceptance;
- web application;
- Linux client;
- production Privacy Shield, Wardveil, Everkeep, Identity, Mesh, or Manager integration;
- accepted Glaze UI conformance.

## Privacy behavior of the current foundation

The Android manifest requests no `INTERNET` permission. The foundation contains no remote telemetry, provider credentials, remote history synchronization, or clipboard monitoring. Android automatic backup is disabled while the Everkeep/recovery model remains unimplemented.

## Development troubleshooting

If the application does not build, treat the exact CI/job output for that source revision as authoritative. Do not infer build acceptance from an older green run after the candidate head changes.

If a capability is shown as `UNKNOWN`, that is intentional fail-closed behavior for a provider capability that has not been declared. The UI must not upgrade it to supported.

## Future manual expansion

This manual must expand only as features become implemented and verified. Planned workflows from the canonical product specification should not be copied here as though they are available.
