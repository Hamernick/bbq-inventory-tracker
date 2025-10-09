# BBQ Inventory Tracker

This project is an Android application built with Kotlin and Jetpack Compose for managing daily BBQ inventory resets. It uses a Gradle wrapper that materializes itself from a bundled Base64 payload so the repository stays free of binary artifacts.

## Getting Started

1. Ensure you have the latest Android Studio or the Android SDK + JDK 17 installed.
2. Clone the repository and from the project root run:
   ```bash
   ./gradlew help --console=plain
   ```
   The first run decodes the Gradle wrapper JAR from the Base64 payload stored in `gradle/wrapper/gradle-wrapper.jar.base64`.
3. Open the project in Android Studio or continue using the command line to build and test:
   ```bash
   ./gradlew assembleDebug
   ./gradlew ktlintCheck detekt
   ```

## Project Structure

The repository follows the guidelines outlined in `AGENTS.md` and includes reusable Compose design primitives, feature screens, and build tooling (ktlint + detekt).
