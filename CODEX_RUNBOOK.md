# CODEX_RUNBOOK — Clover Inventory Week Scheduler (Manual Apply)

## Goal

Ship a lean, reliable Android APK with Splash/Boot, NavHost, WeekGrid placeholder, and Settings — aligned to AGENTS rules: UI → VM → UseCase → Repo → Data; offline-first; Manual Apply only; WorkManager used only to execute Apply queue writes.

---

## 1️⃣ Environment Setup

Requirements

- Android Studio (JDK 17)
- Android SDK API 34–35
- Emulator: Clover tablet profile (1280×800) or Pixel reference device

Commands

```bash
git clone <repo>
cd bbq-inventory-tracker
git checkout -b dev
./gradlew assembleDebug
```

Branching

- `main` → protected
- `dev` → integration branch
- `feat/*` → feature work

---

## 2️⃣ Development Flow

| Phase                         | Goal                         | Deliverables                                                                 | Acceptance                                     |
| ----------------------------- | ---------------------------- | ---------------------------------------------------------------------------- | ---------------------------------------------- |
| M1 — Bootstrap + Splash/Main  | App shell + navigation       | Splash/Boot gate, `NavHost`, placeholder `WeekGrid` and `Settings` screens  | App builds; emulator renders; lint configured   |
| M2 — OAuth (PKCE) + Auth Gate | Login + token store          | PKCE flow, token storage, refresh, bound-device check                        | 401 replay; tokens persisted securely          |
| M3 — Catalog + Room           | Local DB ingest              | Room schema, entities, DAO, ingestion of items/locations                     | DAO tests pass; paging works                   |
| M4 — Week Grid UI             | Editing UX                   | Grid layout, multi-select, dialogs                                           | Smooth scroll; previews compile                 |
| M5 — Apply Queue              | Manual Apply writes only     | Queue-first sync, idempotency keys, WorkManager executor                     | Same write cannot apply twice                   |
| M6 — UX Polish                | A11y + perf + copy           | Labels, contrast, focus order, perf budget                                   | A11y checks pass; no jank                       |
| M7 — Settings + Telemetry     | Admin + logging              | Settings screen finalization, logging/metrics                                | Stability and store polish                      |

Rules carried from AGENTS

- Architecture: UI → VM → UseCase → Repo → Data.
- Offline-first: Room is source of truth.
- Manual Apply: write to Clover only on user action.
- WorkManager: only for Apply queue execution (no timed resets).

---

## 3️⃣ Key Commands

```bash
# Build + install
./gradlew clean assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.bbqreset/.MainActivity

# Lint + tests
./gradlew ktlintCheck detekt testDebugUnitTest

# Feature branch flow
git checkout -b feat/m1-bootstrap
git add -A && git commit -m "feat(m1): splash + nav host + week grid placeholders"
git push -u origin feat/m1-bootstrap
```

---

## 4️⃣ GitHub Actions (CI/CD)

android-ci.yml

```yaml
name: Android CI
on:
  push: { branches: [dev, feat/**] }
  pull_request: { branches: [main] }
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: temurin, java-version: '17' }
      - uses: android-actions/setup-android@v3
      - name: Build
        run: ./gradlew clean ktlintCheck detekt assembleDebug
      - name: Upload APK
        uses: actions/upload-artifact@v4
        with: { name: apk-debug, path: app/build/outputs/apk/debug/*.apk }
```

release.yml

```yaml
name: Release
on:
  push: { tags: [ 'v*.*.*' ] }
jobs:
  bundle:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: temurin, java-version: '17' }
      - uses: android-actions/setup-android@v3
      - name: Build AAB
        run: ./gradlew bundleRelease
      - uses: actions/upload-artifact@v4
        with: { name: aab-release, path: app/build/outputs/bundle/release/*.aab }
```

---

## 5️⃣ Environment & Config

- `local.properties` → SDK path
- `app/src/main/assets/config.json`

```json
{ "sandbox": true, "apiBase": "https://apisandbox.dev.clover.com" }
```

- Secrets: `EncryptedSharedPreferences` (never in git)

---

## 6️⃣ Test Plan

Unit

- Feature-scoped unit tests (UseCases/Repos)
- DAO CRUD tests for Room

UI

- Compose previews compile (Splash, WeekGrid, Settings)

Load

- 10k items scroll target (when grid implemented)

---

## 7️⃣ Release Steps

```bash
git tag v0.1.0 -m "M1: Splash + NavHost + placeholders"
git push origin v0.1.0
```

CI builds → APK artifact uploaded.

---

## 8️⃣ Acceptance Checklist

- CI green (build, tests, lint)
- Manual Apply only (no timed resets)
- Offline-first queue model in place by M5
- No secrets committed; logs redacted; basic a11y

---

## 9️⃣ Next Targets

- Webhook receiver (optional cloud)
- Push notifications via webhook
- PDF report generator stub
- Multi-tenant analytics dashboard
