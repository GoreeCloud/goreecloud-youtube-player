# Architecture

## Current milestone

This repository is beginning as original GoreeCloud-owned software. The first executable slice is an Android application foundation because Android is one of the specification's primary initial platforms and there is a current GoreeCloud Android implementation pattern to validate against.

This milestone is deliberately narrow. It establishes architecture; it does **not** establish YouTube provider compatibility, provider authentication, production playback, downloads, casting, Linux support, television acceptance, or Stable eligibility.

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
        +--> LocalDemoProvider (implemented, network-free, development only)
        |
        +--> YouTube provider (not implemented)
        |
        +--> future authorized providers

Local state contract
        |
        +--> schema-v1.sql (defined)
        +--> runtime database adapter (not implemented)

Integral Platform Systems
        |
        +--> explicit integration boundaries (planned; see PLATFORM-INTEGRATIONS.md)
```

## Provider independence

`ContentProvider` is the product's primary provider boundary. Provider-specific networking, identifiers, authorization, media resolution, and failure behavior must remain behind provider-owned implementation code.

The application must never infer a provider capability merely because a control exists in the UI. `CapabilityResolver` preserves the provider declaration and fails missing declarations to `UNKNOWN`. A consumer may hide or further restrict an operation due to device, privacy, security, identity, or policy state, but it must not upgrade unavailable provider capability to a favorable state.

## Local development provider

`LocalDemoProvider` is deterministic, network-free development infrastructure. It provides enough content and capability declarations to exercise the native application shell without creating an undocumented dependency on YouTube or another external service.

It must never be described as a YouTube implementation.

## Local state

`app/src/main/assets/database/schema-v1.sql` is the initial portable schema contract for local library-oriented state. Runtime SQLite/Room integration, migrations, encryption decisions, Everkeep coverage, data-export behavior, and Privacy Shield authorization remain separate implementation milestones.

The bootstrap disables Android automatic backup because Everkeep/recovery authority has not yet been implemented or accepted for this product.

## User interface and Glaze UI

The executable bootstrap currently uses Jetpack Compose/Material 3 as an implementation substrate so the native shell can compile and be tested. That is **not** evidence of Glaze UI conformance.

The current published Official/Stable/consumer-eligible GoreeCloud target for this revision is Glaze UI 1.3.0. The app must receive a verified current Glaze UI mapping/integration and complete applicable visual, behavioral, accessibility, form-factor, and runtime acceptance before Stable eligibility.

The temporary shell should therefore remain simple and semantically structured. Product-specific visual polish should be added through the canonical Glaze UI contract rather than creating a competing local design system.

## Privacy and network posture

The bootstrap intentionally requests no Android network permission and contains:

- no provider authentication;
- no YouTube requests;
- no telemetry;
- no remote history sync;
- no remote account state;
- no clipboard monitoring;
- no remote playback resolver.

Network access should be introduced only with the provider/purpose boundaries, Privacy Shield behavior, Wardveil requirements, diagnostics rules, and tests needed for that operation.

## Initial package identity

The bootstrap uses `com.goreecloud.youtubeplayer` with the standard Development `.dev` application ID suffix. Package/application-identifier governance remains subject to the authoritative GoreeCloud naming standard; changing the identifier before a Stable release must include migration and documentation review where applicable.

## Next architecture increments

1. Bind the schema through an explicit local persistence adapter and migrations.
2. Implement search/library UI against interfaces rather than provider-specific code.
3. Establish RSS subscription parsing/refresh behind a feed provider boundary.
4. Define media-source/playback contracts before implementing a remote provider.
5. Integrate the current published Stable Glaze UI release through its authoritative consumer contract and verify rendered behavior.
6. Add Privacy Shield, Wardveil Security, Everkeep, Manager, Mesh, and Identity contracts according to their current authoritative interfaces.
7. Add Android TV input/focus adaptation and real-device acceptance.
8. Establish the Linux desktop client using a separately validated native technology choice while preserving the language-neutral provider contracts.
