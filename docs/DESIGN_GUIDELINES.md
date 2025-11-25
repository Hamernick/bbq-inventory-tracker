# Design Guidelines (Compose UI)

Principles
- Token-driven: never hardcode colors/spacing in components; use tokens.
- Content color semantics: within interactive components, derive text/icon color from `LocalContentColor`.
- Contrast & a11y: meet WCAG AA; maintain minimum touch target 48x48dp.
- DRY: reusable primitives (`BBQCard`, `BBQButton`, chips) compose larger patterns.

Color & Theme (shadcn/Tailwind inspired)
- Pair every surface color with a `*Foreground` color.
- Use `extendedColors` tokens (primary, secondary, destructive, muted, border, etc.).
- Never read typography fixed `color` inside components; prefer `LocalContentColor`.
- Light container border: `extendedColors.border`; avoid elevation shadows by default.

Typography
- Use `extendedTypography` for size/weight; avoid fixed color in component text.
- Apply emphasis via `LocalContentColor` alpha when needed (e.g., `onSurfaceVariant`).

Layout
- Spacing scale: xs=4, sm=8, md=12, lg=16, xl=24.
- Corners: 12–16dp for cards/strips; chips 8dp.
- Use `Modifier.weight(...)` only on direct Row/Column children; never read parentData.

Components
- Button (`BBQButton`):
  - Filled variants set `contentColor`; inner text uses `LocalContentColor`.
  - No elevation; 1dp border only for OUTLINE.
- Card (`BBQCard`):
  - Border 1dp, no shadow. Tonal variant uses `surfaceVariant`.
- Date Strip (calendar):
  - Container: light muted surface + 1dp border, 12dp corners.
  - Chip selected: white bg, border; unselected: transparent bg, border, onSurfaceVariant text.
  - Responsive width; center selected; animate color changes.

Compose/M3
- Opt into `ExperimentalMaterial3Api` at function scope for experimental APIs.
- Use `animate*AsState` for subtle transitions.

HIG Alignment
- Clear hierarchy, generous whitespace, consistent affordances.
- Predictable navigation (top app bar + modal drawer).

