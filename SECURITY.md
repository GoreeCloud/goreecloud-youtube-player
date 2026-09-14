# GoreeCloud YouTube Player — Security

## Current security boundary

This repository is Active Development and is not production-approved.

The Android foundation intentionally minimizes attack surface:

- no Android `INTERNET` permission;
- no provider authentication or token storage;
- no remote provider networking;
- no telemetry pipeline;
- no clipboard monitoring;
- no remote playback resolver;
- Android automatic backup disabled while recovery authority is unimplemented.

The deterministic local development provider is not a security boundary for a future network provider. Future remote capabilities require their own threat analysis, authorization, input validation, transport, credential, privacy, and failure-isolation controls.

## Platform-system status

Wardveil Security, Privacy Shield, Everkeep, GoreeCloud Identity, GoreeCloud Mesh, and GoreeCloud Manager are not accepted runtime integrations for this application. Glaze UI targeting is likewise not a security or conformance claim.

## Dependency and provider expectations

- Product-defining application behavior must remain GoreeCloud-owned.
- Narrow third-party libraries may be used when justified, but should remain behind controlled boundaries where practical.
- Provider input must be treated as untrusted until validated.
- Provider capability absence must fail to `UNKNOWN`/unavailable rather than becoming implicit authorization.
- Credentials, tokens, signing material, recovery codes, and production environment files must never be committed.

## Vulnerability reporting

Do not publish reusable secrets, private user data, exploit details, or sensitive production information in a public issue. Use the current approved private GoreeCloud security-reporting channel for exploitable vulnerabilities. If no private reporting channel is available to the reporter, disclose only enough publicly to request a private contact path without including exploit or secret material.

## Release boundary

A green build does not establish security acceptance. Production/Stable eligibility requires applicable Wardveil Security, Privacy Shield, recovery, dependency, signing, platform, runtime, and release evidence for the exact candidate.
