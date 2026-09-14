# GoreeCloud YouTube Player — Development Notes

## Repository recovery

The canonical repository was restored on September 14, 2026 after an earlier deletion/unavailability event. Default-branch CI was re-established through PR #3 and exact-head validated before and after integration.

Historical pre-loss CI evidence remains historical; current candidates require fresh exact-head validation.

## Foundation state

PR #1 is the native Android foundation integration candidate. It establishes architecture and Development evidence only.

PR #2 is stacked Milestone 1 local-data work and must remain separate until the foundation is accepted, rebased/retargeted as needed, and independently validated.

## Current known gaps

- No remote provider implementation.
- No production playback.
- No first-class web client yet, although current GoreeCloud delivery governance requires one.
- No Linux implementation yet.
- No Android TV acceptance.
- No approved product-specific branding asset.
- No approved public software license.
- No accepted Glaze UI application conformance.
- No runtime acceptance for Privacy Shield, Wardveil Security, Everkeep, Manager, Mesh, or Identity.
- Canonical Drive project specification still requires recovery-state reconciliation after repository integration state is verified.

## Documentation rule

Keep repository documentation version-coupled and truthful. Planned capability belongs in specifications/roadmap; user manuals and implementation sections should describe only verified current behavior.
