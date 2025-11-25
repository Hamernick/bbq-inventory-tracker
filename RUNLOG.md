# RUNLOG — BBQ Inventory Tracker (Week Planner, Manual Apply)

## Current State

- App shell with Splash → NavHost → WeekGrid + Settings.
- Week planning persisted in Room (`week_plans`, v2 migration) with edit (+/–), week navigation, and location cycle.
- Prefill from first template for location (writes to `week_plans`).
- Manual Apply queue: enqueues a unique WorkManager job which reads `week_plans` and prepares stock updates.
- Networking ready: Retrofit + OkHttp + Moshi, AuthInterceptor, Clover API stubs (Inventory, Orders, Merchant).
- Apply worker builds idempotent keys per item and calls `CloverInventoryApi.updateStock(...)` (token/merchant are stubs for now).
- Lint/detekt/tests are green (temporary baseline + a few local suppressions where needed).

## Recommended Next Steps

1) Secure Token + Merchant Providers
- Implement `TokenProvider` and `MerchantIdProvider` backed by `EncryptedSharedPreferences`.
- Add simple setter flows under Settings (bind device, paste token for sandbox).

2) Environment & Config
- Introduce `ApiConfig` source (assets or `BuildConfig`) for sandbox/prod base URL.
- Add a quick Settings switch for environment for non‑prod builds.

3) Apply Worker Hardening
- Add retry/backoff policy mapping (429, 5xx) and a capped retry loop per item.
- Surface per‑item failures into `logs` and a simple status sheet/toast.
- Keep idempotency headers; store last sync time for audit.

4) WeekGrid UX Enhancements
- Template picker dialog (choose template for prefill).
- Proper location picker (dialog) and a friendly week label (e.g., “Week of Oct 6”).
- Inline numeric input for quantities in addition to +/-.

5) DI Cleanup
- Introduce a tiny provider or Hilt modules for Database + Network + Providers to avoid constructing in Worker/VM directly.

6) Lint/Detekt Tightening
- Remove baseline and local suppressions incrementally; address long methods and formatting.

7) Tests
- Unit tests: `WeekPlanRepository`, `LoadWeekPlanUseCase`, `SetWeekQuantityUseCase`, and Apply job enqueue behavior.
- Worker tests with fakes for DAO and API (no network) asserting idempotency and error handling.

8) CI Follow‑ups
- Ensure secrets are not required for CI; keep Apply tests offline with fakes.

## How To Build, Install, and Run (Emulator)

Prereqs
- Android Studio with SDK 34/35 and an AVD (e.g., Pixel 7 / API 34) or a connected device.
- Java 17 on PATH (Gradle wrapper will handle toolchains).

Commands (Windows PowerShell)
- Build debug APK:
  - `./gradlew.bat clean assembleDebug`
- Start an emulator (if none is running):
  - List AVDs: `& "$Env:ANDROID_SDK_ROOT\emulator\emulator.exe" -list-avds`
  - Launch: `& "$Env:ANDROID_SDK_ROOT\emulator\emulator.exe" -avd <YourAvdName>`
  - Alternatively, start from Android Studio’s Device Manager.
- Verify device is online:
  - `adb devices`
- Install APK to emulator/device:
  - `adb install -r app/build/outputs/apk/debug/app-debug.apk`
  - If you see a version downgrade error, uninstall first: `adb uninstall com.bbqreset`
- Launch the app:
  - `adb shell am start -n com.bbqreset/.MainActivity`

What you should see
- Splash for a brief moment, then Week Planner screen.
- Use +/- to adjust quantities, Prev/Next to change week, Location to cycle, Prefill to copy from template, Apply to enqueue the manual sync job.

Notes
- Network calls require non‑stub `TokenProvider` and `MerchantIdProvider`; until then, Apply worker will short‑circuit with a job error (“Missing merchant or token”) but will not crash.

## 2025-11-25
- Added HTTPS OAuth redirect intent-filter for `inventoryreset.com/oauth/callback`; set default redirect constant.
- Removed scaffold top app bar to eliminate blank header space and align controls to screen top.
