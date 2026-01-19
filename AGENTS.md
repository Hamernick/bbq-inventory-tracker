# CODEX_RUNBOOK — Clover Inventory Week Scheduler (Execution Manual)

---

## 0) Purpose of this Runbook

This runbook defines **how Codex must build and maintain the Clover Inventory Week Scheduler Android app**. It converts the product specification in `AGENTS.md` into an **executable, repeatable engineering workflow**. The runbook has three goals:

1. **Deterministic execution** — Codex always knows the next step.
2. **Consistency** — architecture, patterns, and code quality never drift.
3. **Elimination of old logic** — previous app behaviors are explicitly removed.

---

## 1) Core Build Rules for Codex

Codex must follow these rules at all times:

* **Architecture rule:** `UI → VM → UseCase → Repo → Data`. No skipping layers.
* **Offline-first rule:** Room is source of truth. Network writes go through sync queue.
* **Manual Apply rule:** Write to Clover **only** when user taps Apply. No automation.
* **Quality rule:** detekt + ktlint must pass before any PR is considered complete.
* **Code size rule:** no file exceeds **400 LOC**.
* **Testing rule:** each feature shipping in a milestone must include unit tests.
* **Refusal rule:** if a request violates AGENTS or this RUNBOOK, Codex must reject it and refactor in order to abide.

---

## 2) UNDO / REMOVE / REFACTOR Directives (from old runbook)

Codex must eliminate all traces of the old application design. Specifically:

**REMOVE:**

* Daily reset logic
* Automatic inventory updates
* Order ingestion, refunds, or sold-quantity calculations
* Scheduler-based automatic apply jobs
* Counter models or start/sold math
* Any references to Orders, Payments, or Analytics flows

**REPLACE:**

* Daily reset scheduler → **Manual Apply flow** only
* Counter-based math → **direct quantity planning**

**ENSURE:**

* WorkManager is only used to **execute Apply queue writes**, not timed resets
* Sync model remains **queue-first, idempotent, and user-triggered only**

No code, documentation, or behavior may contradict these rules going forward.

---

## 3) Environment Setup

**Requirements:**

* Android Studio | JDK 17 | SDK 34
* Emulator: Clover tablet profile (1280×800) + Pixel reference device

**Clone & build:**

```
git clone <repo>
cd project
./gradlew assembleDebug
```

**Clover Sandbox Setup:**

* Create sandbox merchant
* Create app in Clover Dev Dashboard
* Configure Redirect URI for PKCE auth

### Boot/Auth/Main Setup (required)

**Splash/Boot**

* On cold start: verify **min app version**, verify **permissions** (network; notifications optional), ensure **secure storage** and **Room migrations** ready.
* Check token + merchant binding. If valid → **Main**. If missing/expired → **Auth**.

**Auth Gate**

* OAuth scopes: `inventory`, `inventory.read`, `merchant.read`, `employees.read` only. Do not request Orders or Payments scopes.
* If device not bound: run **PKCE**: code → token; fetch **Merchant** (incl. TZ) and **Employee**; persist `{merchantId, tz, employeeId}` and token set in EncryptedSharedPreferences.
* If bound but token invalid: **refresh**; on failure → Auth.

**Main Shell**

* `NavHost` with routes: **WeekGrid**, **Settings** (and dialogs for Add Items / Edit Qty).
* TopAppBar: **Location dropdown**, **Week selector**, **Apply** action.

**DoD (Boot/Auth/Main)**

* Splash visible **<500ms**; cold start **<1.2s**; navigation path logged.
* Deep link for OAuth redirect (`com.bbqreset://oauth/callback`) reaches Auth callback and continues to Main.

---

## 4) Branching, Commits, and PR Rules

* `main` — protected
* `dev` — integration branch
* `feat/<name>` — feature work only

**Commit rules:**

* Small, atomic commits
* Descriptive messages (`feat:`, `fix:`, `refactor:`)
* No direct commits to `main`

**PR rules:**

* Must pass `./gradlew ktlintCheck detekt testDebugUnitTest`
* Must follow architecture rules before merge

---

## 5) Build, Run, and Lint Commands

```
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.bbqreset/.MainActivity

./gradlew ktlintCheck detekt testDebugUnitTest
```

---

## 6) Development Workflow (Codex Execution Loop)

Codex must follow this loop for each task:

1. **Read AGENTS.md and identify scope**
2. **Check RUNBOOK constraints** (architecture, rules, forbidden logic)
3. **Generate or modify files** (never dumping everything into one file)
4. **Add tests** for the new logic
5. **Run lint + tests** and fix violations
6. **Produce final patch** (clean, minimal, compile-ready)

No feature is considered complete without a passing build and tests.

---

## 7) Milestones (Build Order)

| Milestone                         | Result                                                                                                            |
| --------------------------------- | ----------------------------------------------------------------------------------------------------------------- |
| **M1 — Bootstrap + Splash/Main**  | Project scaffold, **Splash/Boot**, NavHost, placeholder **Main** (WeekGrid placeholder), detekt/ktlint configured |
| **M2 — OAuth (PKCE) + Auth Gate** | PKCE flow, token store, refresh, **bound-device check** (merchant/employee), 401 replay                           |
| **M3 — Catalog + Room**           | Local DB, item + location ingest, paging                                                                          |
| **M4 — Week Grid UI**             | Grid layout, editing, multi-select, dialogs                                                                       |
| **M5 — Apply Queue**              | PATCH writes to Clover on **Apply** only                                                                          |
| **M6 — UX Polish**                | Copy defaults, clear week, accessibility, perf budget                                                             |
| **M7 — Settings + Telemetry**     | Final UI, logging/metrics, stability, store polish                                                                |

Each milestone ships independently and must be merged cleanly before the next begins.

-----------|---------|
| **M1 — Bootstrap** | Project scaffold, nav host, placeholder screens |
| **M2 — OAuth (PKCE)** | Login + token store, refresh, 401 replay |
| **M3 — Catalog + Room** | Local DB, item + location ingest, paging |
| **M4 — Week Grid UI** | Grid layout, editing, multi-select |
| **M5 — Apply Queue** | PATCH writes to Clover on Apply |
| **M6 — UX Polish** | Copy defaults, clear week, accessibility |
| **M7 — Settings + Telemetry** | Final UI + logging + stability |

Each milestone ships independently and must be merged cleanly before the next begins.

---

## 8) Sync + Network Execution Rules

* Queue-first: local write → enqueue → POST → reconcile
* Idempotency: same write cannot apply twice
* Retry rules:

    * `401` → refresh + replay once
    * `403` → fail and surface to user
    * `409` → conflict prompt
    * `429` → exponential backoff + jitter
    * `5xx` → capped retry

---

## 9) Error Handling Rules

* Never crash on network failure
* All failures surface through UI state
* Queue must survive app restarts and reboots

---

## 10) Testing Requirements

* Unit tests per feature
* UI tests for dialogs and week grid interactions
* Load test: 10k items must scroll without jank

---

## 11) CI/CD Requirements

* GitHub Actions must:

    * Build
    * Run tests
    * Run detekt + ktlint
    * Upload APK artifact

---

## 12) Release Steps

```
git tag v0.1.0 -m "MVP"
git push origin v0.1.0
```

* Submit to Clover App Market

---

## 13) UI Guidelines - Android/Compose Adaptation of WIG

Use WIG for web reviews. For Android/Compose, apply the mapped rules below and skip web-only items.

**Apply in Compose:**

* Accessibility: Icon-only actions require `contentDescription` or `semantics { contentDescription = ... }`; use `Modifier.clickable`/`Button` instead of raw pointer input for actions; add `semantics { heading() }` for headings.
* Focus: Provide visible focus/pressed states; use `Modifier.focusable()`/`FocusRequester` for keyboard focus and do not remove indicators without replacement.
* Forms: `TextField` must include a label; use `KeyboardOptions` for type and input; do not block paste; show inline errors near the field.
* Animation: Respect reduced motion (use `LocalMotionDurationScale` or system settings); animate only opacity/transform; avoid layout/size animations unless requested.
* Typography: Use `TextOverflow.Ellipsis` with `maxLines`; use `fontFeatureSettings = "tnum"` for tabular numbers where needed.
* Content handling: Long text must truncate or wrap; use `LazyColumn`/`LazyGrid` for large lists; avoid expensive work inside composables.
* Images: Provide `contentDescription`; set an explicit size or constraints; avoid remote-only hero images for critical UI.
* Performance: Avoid layout reads in composition; cache with `remember`/`derivedStateOf`; keep recomposition cheap.
* Navigation & state: Deep-link important screens; reflect key UI state in route args or saved state.
* Touch: Ensure minimum touch target size; provide pressed/hover feedback with `indication` and `interactionSource`.
* Safe areas: Use `WindowInsets` and `systemBarsPadding()`/`safeDrawingPadding()` for full-bleed layouts.
* Locale/i18n: Use locale-aware `DateTimeFormatter` and `NumberFormat`; avoid hardcoded formats.

**Web-only rules to skip in Android:**

* HTML tags/attributes (`<button>`, `<a>`, `aria-*`, `<meta>`, `<link rel=preconnect>`, `loading="lazy"`, `fetchpriority`)
* CSS-only requirements (`color-scheme`, `scroll-margin-top`, `text-wrap`/`text-pretty`)
* Hydration/SSR rules

**End of RUNBOOK**
