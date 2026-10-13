# DevDeck project context

Reviewed for the UI/theme and bundled-content pull request on 2026-10-13.
This describes the implementation, rather than the original project proposal.

## Purpose and structure

DevDeck is John Russel A. Guiwan's developer portfolio, built with Kotlin
Multiplatform and Compose Multiplatform for Android, iOS, and browser Wasm.

- `shared/`: shared presentation, domain, data, dependency injection, resources,
  and platform adapters. Shared Kotlin lives under
  `shared/src/commonMain/kotlin/com/example/devdeck/`.
- `androidApp/`: Android application, manifest, `DevDeckApplication`, and
  `MainActivity`; depends on `shared`.
- `iosApp/`: Xcode/SwiftUI host for the static `Shared` framework. Kotlin targets
  are `iosArm64` and `iosSimulatorArm64`.
- `shared/src/wasmJsMain/`: `ComposeViewport` entry point and HTML loading shell;
  the browser bundle is `devdeck.js`.
- `config/detekt/detekt.yml` and `scripts/check.sh`: static-analysis configuration
  and a lint/Detekt/test convenience script.

There is no `composeApp` module. The Gradle modules are `:androidApp` and `:shared`.

## Runtime flow

1. Android `MainActivity`, iOS `MainViewController`, or Wasm `main` calls `App`.
2. Android initializes Koin in `DevDeckApplication`; `App` starts it when no
   global context exists, supporting the other platforms and previews.
3. `di/AppModule.kt` binds `PortfolioRepositoryImpl` to `PortfolioRepository`,
   registers `BundledPortfolioLoader` explicitly, registers `GetPortfolioDataUseCase`,
   and registers `HomeViewModel`.
4. `PortfolioRepository.getPortfolioData()` returns a cold Flow. Each collection
   reads and validates the bundled JSON once, then emits one `PortfolioData`
   snapshot. Read/decode failures and cancellation propagate to the caller.
   There is no backend, cache layer, or persistent data store.
5. The existing use case delegates to that snapshot Flow.
6. `HomeViewModel` exposes `StateFlow<HomeUiState>` with loading, success, and
   error states. Reloads replace the previous collection. Skill filtering and
   system/light/dark theme selection survive data emissions and retry in memory.
7. `HomeScreen` observes the state and renders the shared Compose interface.

Local content migration Phases 1 and 2 are implemented. The sole production
source of repository content is
`shared/src/commonMain/composeResources/files/portfolio.json`.
`data/BundledPortfolioLoader.kt` reads it via generated `Res.readBytes`, decodes
UTF-8 strictly, validates required fields/content/links, and preserves coroutine
cancellation. Its injectable byte-reader function supports common tests.
`PortfolioData` lives in `domain/model/PortfolioData.kt`. Profile constructor
fields have no personal-content defaults. `@Required` remains on required fields
that still have Kotlin defaults; nullable project links remain optional.
The five old repository methods, five-way combine, and hardcoded production
records have been removed. Host startup and UI/ViewModel behavior are unchanged.

See `LOCAL_CONTENT_IMPLEMENTATION_PLAN.md` for the design and
`LOCAL_CONTENT_PHASE2_RESULTS.md` for current evidence and remaining limitations.

External links are passed through `onOpenUrl`: Android uses an `ACTION_VIEW`
intent with a visible fallback when no handler exists. iOS uses the modern
`UIApplication.openURL` options/completion API. Wasm opens web links with
`noopener,noreferrer` and sends email/phone links to the current window. The `Platform`
expect/actual abstraction supplies platform identity, not URL launching.

## Presentation

The portfolio is one scrolling feed: About, Experience, Projects, Skills, Contact.
Widths below 600 dp use a mobile layout with bottom navigation. Medium widths
(600–839 dp) and expanded widths (840 dp and above) use a sidebar/feed layout.
Section navigation scrolls the list; the visible viewport determines the active
navigation item based on its visible area. The list state is shared across layout
changes. Components use the feed pane's available width to select their layout;
project grids require at least 720 dp of pane width. `PortfolioSection` indices
must match the five list sections. Project cards measure their content naturally
so grid height constraints do not squeeze their action buttons. Skills have
category filters. Browser
accessibility is enabled through Compose's semantics support.

`presentation/ui/components/` contains the profile header, highlights, experience
timeline, project cards, skill filters, contact links, social icons, shared cards,
and pointer glow. `presentation/common/theme/` defines the monochrome Material 3
theme. `Screens.kt` declares screen types, but the current UI does not use a
Navigation Compose graph.

Portfolio records are in `commonMain/composeResources/files/portfolio.json`;
some highlights and UI copy are also embedded in components. Technologies named
in portfolio entries (Firebase, Retrofit, React, etc.) describe showcased work,
not necessarily dependencies of DevDeck itself.

## Build configuration and checks

The current version catalog specifies Kotlin 2.3.20, Compose Multiplatform 1.9.3,
Material 3 1.9.0, Lifecycle 2.9.6, Koin 4.1.1, Android Gradle Plugin 9.0.1, and
Detekt 1.23.8. The Gradle wrapper is
9.1.0. Android uses compile/target SDK 36, minimum SDK 24, and JVM target 11.
Compose/Skiko dependencies resolve without blanket version forces. Preview
tooling is scoped to Android debug builds. Gradle properties disable Android
built-in Kotlin and the new DSL; these legacy options need migration before AGP 10.

Shared models/JSON use `kotlinx-serialization-json` 1.10.0 and the serialization
compiler plugin at the existing Kotlin 2.3.20 version. Android instrumented
tests use AndroidJUnitRunner 1.7.0 and the existing AndroidX test JUnit alias.

Common commands, run from the repository root:

```bash
./gradlew :androidApp:assembleDebug
./gradlew :shared:wasmJsBrowserDevelopmentRun
./gradlew lint detekt
./gradlew :shared:testDebugUnitTest :shared:wasmJsBrowserTest
./gradlew :shared:connectedDebugAndroidTest
bash scripts/check.sh
```

iOS is built through `iosApp/iosApp.xcodeproj` on macOS with Xcode. Common regression
tests cover visible-section selection, reload cancellation, preference retention,
ViewModel cleanup, and project link destinations. They are wired into Android,
Wasm, and iOS tests through `commonTest`; placeholder platform tests were removed.
The check script runs Android and headless Chrome browser tests, and also runs
`iosSimulatorArm64Test` on macOS. iOS runtime verification requires macOS/Xcode.

The 2026-10-09 fix pass verified the Android debug build, all 10 regression tests
on Android and Wasm, Android lint, and release dependencies without full preview
tooling. Detekt passes for `androidApp`; `shared` still reports existing formatting
and naming violations, primarily in portfolio copy and theme files. The iOS URL
API was checked against installed Kotlin bindings, but no iOS runtime was available.

The fresh 2026-10-12 baseline passed all 11 then-current regression tests on
Android and Wasm, Android APK/Wasm production builds, and Android/shared lint.
Shared Detekt reported 183 existing findings. Phase 1 adds common decoder,
validator, read-error, and cancellation tests, plus Android instrumented and
Wasm packaged-read parity tests. Phase 1 passed 25 Android unit tests, 26 browser
tests, and one Android device parity test, plus Android/Wasm builds and lint.
Detekt has the same 183 baseline findings with none added. iOS remains unverified.
See `LOCAL_CONTENT_PHASE1_RESULTS.md` for commands and evidence.

Phase 2 passed 30 Android unit tests, 31 browser tests, one real packaged-read
and DI test on the existing read-only Android emulator, Android/Wasm builds,
and Android/shared lint. Shared Detekt now reports 71 remaining findings;
none are in Phase 2 changed/new code. The browser smoke check verified an HTTP
200 JSON request, rendered content, null-link hiding, skills filtering, light
mode, contact navigation, and retained state across the mobile breakpoint.
iOS compile/runtime and Android visual smoke remain unverified. See
`LOCAL_CONTENT_PHASE2_RESULTS.md` for commands and environment setup.

## Known documentation and content drift

- README copy still describes two production apps, while the repository profile
  says three. Its LinkedIn URL also differs from the repository data.
- Earn PH's unverified Play Store destination and profile-only project repository
  URLs are omitted. Supply verified destinations before adding those buttons.
- README screenshots are placeholders. Build badges are not verification results.
- Profile content defaults were removed during Phase 2. Displayed records now
  come from the validated bundled snapshot.

## Graphify

Graphify (`graphifyy` 0.8.33) was already installed through pipx. A local graph is
now available in `graphify-out/`:

- `graph.json`: queryable structural graph.
- `GRAPH_REPORT.md`: generated topology and extraction report.
- `graph.html`: interactive visualization.

```bash
graphify update .
graphify query "HomeScreen" --budget 1500
graphify explain "PortfolioRepositoryImpl"
```

`graphify update .` rebuilds locally from supported code and document structure,
without an LLM semantic pass. Images are not interpreted. Graph edges can miss
Koin wiring, constructor dependencies, and cross-language bridges, and short
symbol names may resolve ambiguously. Verify claims against the cited source.
The generated report's corpus-size verdict is heuristic; this project is small
enough to read directly. Its commit marker does not prove freshness when the
working tree has uncommitted changes. Refresh after source edits.

## Development rules

Keep platform imports out of `commonMain`; pass platform behavior through shared
interfaces or callbacks. Keep state management in the ViewModel and domain/data
work out of composables. Follow `.agents/rules/anti-slop.md`. Preserve unrelated
working-tree changes, and run checks appropriate to the code being changed.
