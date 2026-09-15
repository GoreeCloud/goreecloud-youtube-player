# GoreeCloud YouTube Player — Feature Roadmap

**Lifecycle:** Active Development  
**Roadmap authority:** synchronized repository counterpart to GoreeCloud Drive → Feature Roadmap → GoreeCloud YouTube Player → `FEATURE-ROADMAP.docx`  
**Canonical product scope:** GoreeCloud Drive → Projects → `Project Specification — YouTube Player.docx`

This roadmap orders work. It does not upgrade planned capabilities into implemented features.

## Milestone 0 — Repository recovery and governed native foundation

- [x] Restore canonical repository identity and source-control continuity.
- [x] Re-establish exact-head/default-branch Android CI.
- [x] Integrate and post-merge validate the governed native Android foundation.
- [x] Reconcile canonical project documentation and task records.
- [x] Retire repository-recovery tracking after authoritative systems agreed.

## Milestone 1 — Local-first durable state

Verified integrated baseline on `main` at `71e078c6ee8ed1c1602d388e77c112e669d39431`:

- [x] Bind schema-v1 to the runtime SQLite persistence adapter.
- [x] Persist watch history and resume positions.
- [x] Provide `GCYTP-LIBRARY` v1 portable export/import with fail-closed validation.
- [x] Establish Android 16 emulator acceptance for schema-v1 initialization, reopen persistence, replacement semantics, and rejected-import preservation.

Current PR #5 Development candidate:

- [ ] Add an explicit v1 → v2 migration path and exact Android migration evidence.
- [ ] Persist Favorites and Watch Later as the next approved local-library record classes.
- [ ] Export `GCYTP-LIBRARY` v2 while preserving import compatibility with v1 exports.
- [ ] Pass exact-head standard Android and emulator runtime validation for schema v2 before integration.
- [ ] Post-merge validate the resulting exact `main` revision before recording v2 runtime acceptance.

Remaining after PR #5:

- [ ] Complete physical-device acceptance for durable local-data behavior.
- [ ] Add user-facing import/export preview with explicit merge/replace choices, accessible failures, and recovery UX.
- [ ] Extend persistence to further approved local-library state with matching migration/portability/test coverage.
- [ ] Define and implement Privacy Shield authorization for durable user state.
- [ ] Define and accept Everkeep backup/restore coverage before recovery claims.

## Milestone 2 — Search, library, and subscriptions

- [ ] Build provider-neutral local library/search interfaces.
- [ ] Add RSS feed provider contracts and safe parsing.
- [ ] Implement channel following and chronological Subscription Inbox.
- [ ] Add folders, tags, unread state, import, and export with explicit local ownership.

## Milestone 3 — Media and provider contracts

- [ ] Define media-source and playback-session contracts before remote provider implementation.
- [ ] Add a capability-aware playback-engine boundary and provider failure taxonomy.
- [ ] Implement a YouTube provider only behind approved networking, privacy, security, and provider-capability boundaries.
- [ ] Keep unsupported, restricted, degraded, and unknown capabilities truthful and fail closed.

## Milestone 4 — Current Glaze UI and accessibility acceptance

- [ ] Integrate the current approved Glaze UI contract; current Official/Stable consumer target is 1.4.0.
- [ ] Verify compact/expanded layout, text scaling, reduced motion/transparency, contrast, keyboard/focus, TalkBack, and switch-access behavior as applicable.
- [ ] Establish Android rendered and representative-device evidence.
- [ ] Revalidate whenever the approved Glaze baseline changes.

## Milestone 5 — Ordered platform expansion

- [ ] Treat any additional platform beyond Android, Linux, and Android TV / Google TV as a separate product decision requiring its own justification and authorization.
- [ ] Expand to a supported native Linux Desktop client after the Android foundation reaches the required Development maturity.
- [ ] After Linux expansion, add the Android TV / Google TV client with purpose-built input, focus, layout, and remote-control behavior.
- [ ] Add Docker/Podman only if a server-hosted or otherwise container-appropriate component is introduced.

## Milestone 6 — GoreeCloud Platform Systems

Evaluate and implement the current applicable contracts for:

- [ ] GoreeCloud Manager
- [ ] Privacy Shield
- [ ] Wardveil Security
- [ ] Everkeep
- [ ] Glaze UI
- [ ] GoreeCloud Mesh
- [ ] GoreeCloud Identity

Documentation/metadata alone cannot satisfy these gates.

## Milestone 7 — Product capability expansion

- [ ] Native Home/discovery controls and user-configurable surfaces.
- [ ] Custom playlists, collections, Watch Later, favorites, notes, tags, and queue management beyond the bounded persistence slices already accepted.
- [ ] Continue Watching, Shorts controls, live/premiere awareness, and granular notifications.
- [ ] URL/share integration, privacy-bounded clipboard handling, capability-aware casting, and authorized offline/local media.
- [ ] Planned privacy-first YouTube/YouTube Music playback objectives, including audio-only/background modes, ad-free objectives, segment controls, and download management, only after provider/legal/privacy/security gates are satisfied.

## Milestone 8 — Release qualification

- [ ] Exact release-candidate build/test evidence on every supported target.
- [ ] Security, privacy, recovery, rollback, signing/distribution, accessibility, and current Glaze UI acceptance.
- [ ] Production/runtime acceptance for every claimed capability.
- [ ] Documentation and roadmap reconciliation before lifecycle promotion.

Stable is prohibited until all applicable governing requirements are implemented, current, validated, and accepted.
