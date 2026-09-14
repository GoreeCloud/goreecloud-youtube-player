# GoreeCloud YouTube Player — Feature Roadmap

**Lifecycle:** Active Development  
**Roadmap authority:** synchronized repository counterpart to GoreeCloud Drive → Feature Roadmap → GoreeCloud YouTube Player → `FEATURE-ROADMAP.docx`  
**Canonical product scope:** GoreeCloud Drive → Projects → `Project Specification — YouTube Player.docx`

This roadmap orders work. It does not upgrade planned capabilities into implemented features.

## Milestone 0 — Repository recovery and governed native foundation

Current objective: establish a recoverable, reviewable native repository baseline.

- [x] Restore canonical repository identity.
- [x] Restore pull-request history and development branches.
- [x] Re-establish exact-head/default-branch Android CI bootstrap.
- [ ] Integrate the documentation-complete native Android foundation through exact-head CI and review.
- [ ] Reconcile canonical Drive specification from recovery-blocked wording to verified restored state.
- [ ] Close repository-recovery tracking only after authoritative source and documentation agree.

## Milestone 1 — Local-first durable state

- [ ] Bind schema-v1 to a runtime persistence adapter.
- [ ] Persist watch history and resume positions.
- [ ] Provide versioned portable export/import with fail-closed validation.
- [ ] Add migration boundaries and tests.
- [ ] Define Privacy Shield authorization for persistent user state.
- [ ] Define Everkeep backup/restore coverage before recovery claims.

The first portion is under stacked Development PR #2 and remains unintegrated until its own exact-head validation passes.

## Milestone 2 — Search, library, and subscription foundations

- [ ] Build provider-neutral local library/search interfaces.
- [ ] Add RSS feed provider contracts and safe parsing.
- [ ] Implement channel following and chronological subscription inbox.
- [ ] Add folders/tags/unread state with explicit local ownership.
- [ ] Add import/export for subscription state.

## Milestone 3 — Media and provider contracts

- [ ] Define media-source and playback-session contracts before remote provider implementation.
- [ ] Add capability-aware playback engine boundary.
- [ ] Add provider failure taxonomy and recovery UX.
- [ ] Add optional YouTube provider only behind approved networking/privacy/security boundaries.
- [ ] Keep unsupported provider capability truthful and fail closed.

## Milestone 4 — Current Glaze UI and accessibility acceptance

- [ ] Integrate current approved Glaze UI contract; current published target is 1.3.0.
- [ ] Verify compact/expanded layout behavior.
- [ ] Verify text scaling, reduced motion/transparency, contrast, keyboard/focus, TalkBack/switch-access behavior as applicable.
- [ ] Establish Android rendered and representative-device evidence.
- [ ] Revalidate whenever the approved Glaze baseline changes.

## Milestone 5 — Mandatory delivery surfaces

- [ ] Build first-class web application sharing authoritative contracts without becoming the native-client implementation.
- [ ] Build supported Linux deployment/client appropriate to product role.
- [ ] Establish Android TV / Google TV input/focus/layout adaptation.
- [ ] Define cross-client shared data/API boundaries without unnecessary duplicate authoritative stores.
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

- [ ] Native Home/discovery controls.
- [ ] Custom playlists, collections, Watch Later, favorites, notes, and tags.
- [ ] Queue management and Continue Watching.
- [ ] Shorts controls.
- [ ] Live stream/premiere awareness.
- [ ] Granular notifications.
- [ ] URL/share integration and privacy-bounded clipboard handling.
- [ ] Capability-aware casting and authorized offline/local-media behavior.

## Milestone 8 — Release qualification

- [ ] Exact release-candidate build/test evidence on each supported target.
- [ ] Security/privacy/recovery reviews and rollback evidence.
- [ ] Current Glaze UI conformance and accessibility acceptance.
- [ ] Controlled signing/distribution and update path.
- [ ] Production/runtime acceptance for every claimed capability.
- [ ] Documentation and roadmap reconciliation.

Stable is prohibited until all applicable governing requirements are implemented, current, validated, and accepted.
