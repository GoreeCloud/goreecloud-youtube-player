# GoreeCloud YouTube Player — Privacy Policy

**Status:** Development source policy; not a production Privacy Shield acceptance record.

## Current foundation behavior

The current Android foundation is intentionally local and network-free:

- no Android `INTERNET` permission is requested;
- no YouTube/provider authentication is implemented;
- no remote provider request is implemented;
- no telemetry or advertising pipeline is implemented;
- no remote watch-history synchronization is implemented;
- no clipboard monitoring is implemented;
- no remote playback resolver is implemented;
- Android automatic backup is disabled.

`LocalDemoProvider` uses deterministic development data and is not a remote provider.

The repository defines a local database schema contract, but the foundation candidate does not yet bind that schema to runtime persistence. Durable progress persistence is separate stacked Development work until integrated and validated.

## Planned data authority

The product direction is local-first. User-organized state such as history, resume positions, follows, playlists, notes, tags, and preferences should remain locally controlled wherever practical.

Future processing must be governed by explicit purpose, data minimization, retention, authorization, and user controls. Network access, provider accounts, telemetry, synchronization, notifications, and cloud/mesh behavior must each cross their own Privacy Shield and security authority boundaries before use.

## Provider separation

Private GoreeCloud application state must remain distinct from Google/YouTube account state unless the user explicitly chooses an integration that requires provider-side authorization. A provider capability or account state must not silently become authorization for unrelated GoreeCloud data use.

## Current non-claims

This source policy does not establish runtime Privacy Shield acceptance, production privacy review, legal/regulatory compliance, or a released product privacy notice. Those require the applicable authoritative review before production use.
