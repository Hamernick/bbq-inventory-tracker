# BBQ Inventory — Design System

This design system is inspired by Maxime Heckel’s Design System and adapted to Android Jetpack Compose.

Goals
- Token-first: Colors, typography, spacing, radius, elevation, motion.
- CompositionLocals provide tokens across the app.
- Components consume tokens; no hardcoded colors/sizes.
- High contrast defaults, Apple HIG-aligned spacing and shape rhythm.

Package
- `app/src/main/java/com/bbqreset/ui/design/system/DesignSystem.kt`
  - Tokens: `DSColors`, `DSTypography`, `DSSpacing`, `DSRadius`, `DSElevation`, `DSMotion`, `DSShapes`
  - Locals: `LocalDSColors`, `LocalDSTypography`, etc.
  - `DSTheme { … }` to provide tokens.
- Integrated in `BBQTheme` so all screens get DS tokens automatically.

Tokens (defaults)
- Colors: primary/onPrimary, surface/onSurface, surfaceVariant/onSurfaceVariant, border, muted/mutedForeground, destructive/onDestructive.
- Typography: display, titleLg/Md/Sm, bodyLg/Md, labelMd/Sm.
- Spacing: xs=4, sm=8, md=12, lg=16, xl=24.
- Radius: sm=8, md=12, lg=16, full.
- Elevation: level0/1/2 (use borders over shadows by default).
- Motion: 120/200/320ms, standard ease-in-out curve.
- Shapes: chip=8dp, container=12dp, card=16dp.

Usage
```kotlin
@Composable
fun SomeComposable() {
  val colors = LocalDSColors.current
  val space = LocalDSSpacing.current
  val shapes = LocalDSShapes.current
  Surface(color = colors.surface, shape = shapes.card) { /* ... */ }
}
```

Guidelines
- Derive text/icon color from `LocalContentColor` inside interactive components.
- Prefer borders over elevation; keep shadows subtle.
- Don’t read weights or internal parent data; only set `Modifier.weight()` on direct children.
- Opt-in to Material3 experimental APIs at the smallest scope.

Roadmap
- Semantic color roles (success/warn/info) and state layers.
- Dark theme DS tokens.
- Motion specs per component.

