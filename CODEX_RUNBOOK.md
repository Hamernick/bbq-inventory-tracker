# CODEX_RUNBOOK — BBQ Inventory Tracker

## Goal

From blank repo → runnable Kotlin Android APK with Today, Templates, Logs, Settings, Scheduler, and Clover API stubs.

---

## 1️⃣ Environment Setup

**Requirements**

* Android Studio + JDK 17
* Android SDK API 34
* Emulator: Pixel 7 (API 34)
* Repo: [https://github.com/Hamernick/bbq-inventory-tracker](https://github.com/Hamernick/bbq-inventory-tracker)

**Commands**

```bash
git clone https://github.com/Hamernick/bbq-inventory-tracker.git
cd bbq-inventory-tracker
git checkout -b dev
./gradlew assembleDebug
```

**Branching**

* `main` → protected
* `dev` → integration branch
* `feat/*` → feature work

---

## 2️⃣ Development Flow

| Phase              | Goal                 | Deliverables                                   | Acceptance                        |
| ------------------ | -------------------- | ---------------------------------------------- | --------------------------------- |
| **M1 Bootstrap**   | Compose UI shell     | `MainActivity`, shadcn primitives, TodayScreen | App builds, emulator renders      |
| **M2 Room Schema** | Local DB             | Entities, DAO, Migration v1                    | DAO tests pass                    |
| **M3 Templates**   | Prep templates       | CRUD + apply logic                             | State persists; toast + log entry |
| **M4 Scheduler**   | Daily reset          | WorkManager job + tz aware                     | Worker idempotent per (date, loc) |
| **M5 Counters**    | Track sold/remaining | Domain math, Today UI updates                  | Unit tests green                  |
| **M6 Orders Stub** | Ingest sales         | OrdersRepo, fixtures, refund reversal          | Test delta calc                   |
| **M7 Alerts**      | Threshold warnings   | Notifier interface + Toast impl                | Toast shows at threshold          |
| **M8 Analytics**   | Trends & export      | Charts + CSV writer                            | CSV downloads                     |
| **M9 Clover API**  | Retrofit wiring      | AuthInterceptor + Inventory/Orders APIs        | Handles 401/retry                 |
| **M10 Hardening**  | QA & CI              | Tests, detekt, ktlint, coverage                | CI green                          |

---

## 3️⃣ Key Commands

```bash
# Build + install
gradlew clean assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.bbqreset/.MainActivity

# Lint + tests
./gradlew ktlintCheck detekt testDebugUnitTest

# Feature branch flow
git checkout -b feat/templates
git add -A && git commit -m "feat: templates screen"
git push -u origin feat/templates
```

---

## 4️⃣ GitHub Actions (CI/CD)

**.github/workflows/android-ci.yml**

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

**.github/workflows/release.yml**

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

* `local.properties` → SDK path
* `app/src/main/assets/config.json`

```json
{ "sandbox": true, "thresholdDefault": 5, "apiBase": "https://apisandbox.dev.clover.com" }
```

* Secrets: `EncryptedSharedPreferences`

---

## 6️⃣ Test Plan

**Unit Tests**

* `ResetPlannerTest`, `SchedulerTest`, `CountersTest`, DAO CRUD.
  **Integration**
* Template apply → verify counters.
  **UI Previews**
* Compose previews compile (Today, Templates, Logs).

---

## 7️⃣ Release Steps

```bash
git tag v0.1.0 -m "MVP UI + Scheduler"
git push origin v0.1.0
```

CI builds → artifact uploaded → optional Appetize.io upload.

---

## 8️⃣ Acceptance Checklist

* ✅ CI green (build, tests, lint)
* ✅ Core flows: Apply Template, Daily Reset, Adjust, Alerts, Export CSV
* ✅ No secrets committed
* ✅ a11y: labels, contrast, focus order
* ✅ Logs redact tokens

---

## 9️⃣ Next Targets

* Clover webhook receiver service (optional cloud)
* Push notifications via webhook
* PDF report generator
* Multi-tenant analytics dashboard
