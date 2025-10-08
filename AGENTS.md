# AGENTS — BBQ Inventory Tracker (Android/Kotlin, Jetpack Compose, Clover)

**Mission:** Ship a lean, reliable Clover‑POS Android app (Kotlin) that resets daily stock, tracks sell‑through, alerts on lows, supports multi‑location, and exports reports.

---

## 0) Ground Rules

* Output **whole files**. Keep diffs minimal after bootstrap.
* Kotlin + Jetpack Compose + Room + WorkManager + Retrofit/OkHttp. **No GMS/Firebase.**
* Idempotency for jobs & network ops. Deterministic time via `Clock`.
* ≥80% unit coverage for **domain** (`ResetPlanner`, counters, scheduler).
* Secrets: `EncryptedSharedPreferences`; never in git.

## 1) Repo Layout (target)

```
app/
  src/main/java/com/bbqreset/
    core/        (Clock, DI, Result)
    data/
      api/       (CloverInventoryApi, CloverOrdersApi, CloverMerchantApi, AuthInterceptor)
      db/        (AppDb, Migrations, Dao/*)
      repo/      (InventoryRepo, OrdersRepo, TemplateRepo, CounterRepo, LogRepo)
    domain/      (models, ResetPlanner, Counters, Schedules)
    ui/
      design/    (shadcn tokens, Button, Card, Input, Tabs, Badge, Sheet, Toast)
      screens/   (Today, Templates, Logs, Settings, Analytics)
      nav/       (NavHost)
    work/        (DailyResetWorker, Scheduler, RetryQueue)
    util/        (Idempotency, Csv, PdfStub, Notifier)
  src/main/res/  (strings, themes)
docs/ (PRD.md, API.md, FLOWS.md)
```

## 2) Data Model (Room v1)

* `locations(id, name, tz)`
* `items(id, clover_item_id, name, sku, location_id)`
* `templates(id, name, location_id, holiday_code)`
* `template_items(template_id, item_id, start_qty)`
* `counters(date, item_id, location_id, start_qty, sold_qty, manual_adj, closed_on)`
* `logs(id, ts, actor, action, meta_json)`
* `jobs(id, kind, scheduled_for, status[pending|running|done|error], last_error, dedupe_key)`

Indexes: `(date,item_id,location_id)`, `template_items(template_id,item_id)`, `items(clover_item_id)`.

## 3) Feature → Deliverables

1. **Daily Reset Scheduler**

   * `DailyResetWorker` runs at open (tz aware); **one run/day/location** (dedupe by `(date, location_id)`).
   * `Scheduler.schedule(openHour, openMinute, tz)` via WorkManager.
   * Writes `start_qty` into `counters`; optional push to Clover stock.

2. **Inventory Templates**

   * CRUD + holiday variants; "Apply" now/later.
   * Screen UX: list → edit (items + qty) → apply.

3. **Stock Sync & Tracking**

   * Orders ingest (poll cursor; webhook‑ready).
   * Map line items → items; update `sold_qty`; reverse refunds.
   * Optional auto **sold‑out** when `on_hand <= 0`.

4. **Low‑Stock Alerts**

   * Per‑item threshold; `Notifier`: `ToastNotifier`, `WebhookNotifier` (POST hook).

5. **Multi‑Location**

   * Switcher; templates/counters scoped; manager overview card.

6. **Shift Log**

   * Log reset/apply/adjust/alert events → `logs` screen with filters.

7. **Analytics & Export**

   * Today summary (remaining, sold, waste%). Trends 7/30d (simple Canvas chart).
   * CSV export; `PdfStub` placeholder.

8. **Settings / Admin**

   * Hours + tz, include/exclude items, thresholds; Manager/Staff (UI gated).

## 4) Integration (Clover)

* Retrofit base URLs (sandbox/prod). `AuthInterceptor` (token placeholder).
* APIs: Inventory (items, stocks, availability), Orders (orders, lineItems, payments), Merchant (tz).
* Network: TLS, timeouts, retry/backoff; idempotency keys for stock updates.

## 5) Testing

* Unit: `ResetPlannerTest`, `CountersTest`, `SchedulerTest`, DAO tests.
* UI: previews compile; minimal instrumentation for DAO flows.

## 6) CI

* GitHub Actions: build, tests, ktlint, detekt; upload APK artifact; optional Appetize upload.

---

## 7) Milestones (agent queue)

### M1 — Bootstrap (UI Shell)

**Tasks**

* Create Gradle/Compose project; add `ui/design` primitives (shadcn‑style).
* `MainActivity` → `TodayScreen` with sample items & actions.
  **DoD**
* `./gradlew assembleDebug` green; emulator renders screen; ktlint/detekt configured.
  **Prompts**
* *“Scaffold Compose app + shadcn primitives (buttons, card, input, tabs, badge, sheet, toast) and Today screen. Output full files.”*

### M2 — Room v1

**Tasks**

* Entities/DAO/migration for tables in §2; `AppDb` + DI; Debug seeder.
  **DoD**
* DAO unit tests; app lists seeded items.
  **Prompts**
* *“Generate Room schema and DAO with Migration v1 + tests per AGENTS.”*

### M3 — Templates

**Tasks**

* Templates screen (list/edit); apply template → upsert `counters(start_qty)`; log event.
  **DoD**
* State persists; Apply shows toast + log entry.
  **Prompts**
* *“Build Templates screen + apply flow, persisting to Room; full files.”*

### M4 — Scheduler

**Tasks**

* `DailyResetWorker` + `Scheduler.schedule()`; tz‑aware; idempotent by `(date, location_id)`.
  **DoD**
* Unit tests for time calc & idempotency; manual trigger in Debug.
  **Prompts**
* *“Implement DailyResetWorker/Scheduler with idempotency + tests.”*

### M5 — Counters Math

**Tasks**

* Pure funcs: `on_hand = start_qty - sold_qty + manual_adj`.
* Today screen shows badges & quick adjust.
  **DoD**
* Unit tests for edge cases.
  **Prompts**
* *“Add counters domain with tests; wire to Today UI.”*

### M6 — Orders Ingest (stub)

**Tasks**

* `OrdersRepo` polling cursor; fixtures; update `sold_qty`; refund reversal.
  **DoD**
* Unit tests pass; log deltas.
  **Prompts**
* *“Implement OrdersRepo with cursor + tests; reverse refunds.”*

### M7 — Sold‑Out & Alerts

**Tasks**

* Toggle sold‑out (local flag; later via API). Threshold alerts via `Notifier`.
  **DoD**
* Threshold triggers toast + log; flag visible.
  **Prompts**
* *“Add Notifier + thresholds; wire to Today.”*

### M8 — Analytics & Export

**Tasks**

* Summary cards; 7/30d trends; CSV export; PDF stub.
  **DoD**
* CSV saved to external files + share intent.
  **Prompts**
* *“Implement analytics cards + CSV exporter utility.”*

### M9 — Clover Retrofit (wire real)

**Tasks**

* Replace stubs with Retrofit APIs; `AuthInterceptor`; env flags.
  **DoD**
* Network errors handled; retry/backoff; idempotent updates.
  **Prompts**
* *“Create Clover Retrofit clients (Inventory/Orders/Merchant) + interceptor + env switch.”*

### M10 — Hardening

**Tasks**

* EncryptedSharedPrefs; retry queue; accessibility labels; clean logs.
  **DoD**
* Lint/ktlint/detekt green; tests ≥80% domain.
  **Prompts**
* *“Harden: secrets, retry queue, a11y, logging; raise coverage.”*

---

## 8) Coding Standards

* Compose: small, pure composables; `@Preview` per screen.
* Repo pattern (`Repo → DAO/API → domain`).
* Suspend functions; OkHttp timeouts; Moshi DTOs.
* No magic numbers; constants or tokens.

## 9) Run/Debug (quick)

```
./gradlew assembleDebug && adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.bbqreset/.MainActivity
```

## 10) Acceptance (release)

* CI green; APK attached.
* Core flows: Apply Template, Daily Reset, Adjust, Alerts, Export CSV.
* No secrets; logs redacted; basic a11y.
